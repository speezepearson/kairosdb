package org.kairosdb.datastore.cassandra;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.kairosdb.core.datastore.setvaluedtags.SetValuedTagPredicate;

import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class TagMatcher implements Predicate<DataPointsRowKey> {

    private final SetMultimap<String, String> m_filterTags;
    private final Map<String, SetValuedTagPredicate> m_filterSetValuedTags;
    private final Set<String> m_filterTagNames;
    private Map<String, Pattern> m_patternFilter;

    public TagMatcher(SetMultimap<String, String> filterTags,
                      Map<String, SetValuedTagPredicate> filterSetValuedTags) {
        this(filterTags, filterSetValuedTags, "");
    }

    public TagMatcher(SetMultimap<String, String> filterTags,
                      Map<String, SetValuedTagPredicate> filterSetValuedTags,
                      String regexPrefix) {

        m_filterTags = HashMultimap.create();
        m_filterTagNames = new HashSet<>();
        m_filterSetValuedTags = filterSetValuedTags;
        m_patternFilter = new HashMap<>();

        for (Map.Entry<String, String> entry : filterTags.entries())
        {
            if (regexPrefix.length() != 0 && entry.getValue().startsWith(regexPrefix))
            {
                String regex = entry.getValue().substring(regexPrefix.length());

                Pattern pattern = Pattern.compile(regex);

                m_patternFilter.put(entry.getKey(), pattern);
            }
            else
            {
                m_filterTags.put(entry.getKey(), entry.getValue());
            }

            m_filterTagNames.add(entry.getKey());
        }

    }

    @Override
    public boolean test(DataPointsRowKey rowKey) {
        Map<String, String> stringTags = Maps.newHashMap();
        Map<String, ImmutableSet<String>> setTags = Maps.newHashMap();
        unpackTags(rowKey.getTags(), stringTags, setTags);

        for (String tag : m_filterTagNames)
        {
            String value = stringTags.get(tag);
            if (value == null || !(m_filterTags.get(tag).contains(value) ||
                    matchRegexFilter(tag, value)))
                return false;
        }

        for (Map.Entry<String, SetValuedTagPredicate> entry : m_filterSetValuedTags.entrySet()) {
            Set<String> value = setTags.get(entry.getKey());
            if (!entry.getValue().matches(value))
                return false;
        }

        return true;
    }

    private boolean matchRegexFilter(String tag, String value)
    {
        if (m_patternFilter.containsKey(tag))
        {
            Pattern pattern = m_patternFilter.get(tag);

            return pattern.matcher(value).matches();
        }
        return false;
    }

    /* package private */ static final String MAGIC_TAG = "__set_valued_tags__";
    private static void unpackTags(Map<String, String> packedTags, Map<String, String> stringTags, Map<String, ImmutableSet<String>> setTags) {
        if (!packedTags.containsKey(MAGIC_TAG)) {
            stringTags.putAll(packedTags);
            return;
        }

        Set<String> setValuedTagNames = decodeSetValuedTag(packedTags.get(MAGIC_TAG));
        for (String key : setValuedTagNames) {
            setTags.put(key, ImmutableSet.copyOf(decodeSetValuedTag(packedTags.get(key))));
        }

        for (Map.Entry<String, String> entry : packedTags.entrySet()) {
            if (entry.getKey().equals(MAGIC_TAG)) {
                continue;
            } else if (setValuedTagNames.contains(entry.getKey())) {
                setTags.put(entry.getKey(), ImmutableSet.copyOf(decodeSetValuedTag(entry.getValue())));
            } else {
                stringTags.put(entry.getKey(), entry.getValue());
            }
        }
    }

    private static final Gson gson = new Gson();
    private static Set<String> decodeSetValuedTag(String encoded) {
        return gson.fromJson(new StringReader(encoded), new TypeToken<Set<String>>(){}.getType());
    }

}
