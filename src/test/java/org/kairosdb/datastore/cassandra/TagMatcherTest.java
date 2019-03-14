package org.kairosdb.datastore.cassandra;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.SetMultimap;
import org.junit.Test;
import org.kairosdb.core.datastore.setvaluedtags.ContainsAllPredicate;
import org.kairosdb.core.datastore.setvaluedtags.SetValuedTagPredicate;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TagMatcherTest {

    private static final SetMultimap NO_STR_TAGS = HashMultimap.create();
    private static final Map<String, SetValuedTagPredicate> NO_SET_TAGS = new HashMap<>();

    private DataPointsRowKey createKey(SortedMap<String, String> tags) {
        return new DataPointsRowKey("myMetric", "myCluster", 50, "myDataType", tags);
    }

    @Test
    public void test_empty_filters() {
        TagMatcher matcher = new TagMatcher(NO_STR_TAGS, NO_SET_TAGS);
        assertTrue(matcher.test(createKey(ImmutableSortedMap.of())));
        assertTrue(matcher.test(createKey(ImmutableSortedMap.of("k1", "v1"))));
    }

    @Test
    public void test_string_valued_filters() {
        TagMatcher matcher = new TagMatcher(ImmutableSetMultimap.of("k1", "valid"), NO_SET_TAGS);
        assertFalse(matcher.test(createKey(ImmutableSortedMap.of())));
        assertTrue(matcher.test(createKey(ImmutableSortedMap.of("k1", "valid"))));
        assertFalse(matcher.test(createKey(ImmutableSortedMap.of("k1", "invalid"))));
    }

    @Test
    public void test_multi_string_valued_filters() {
        TagMatcher matcher = new TagMatcher(ImmutableSetMultimap.of("k1", "valid1", "k1", "valid2"), NO_SET_TAGS);
        assertFalse(matcher.test(createKey(ImmutableSortedMap.of())));
        assertTrue(matcher.test(createKey(ImmutableSortedMap.of("k1", "valid1"))));
        assertTrue(matcher.test(createKey(ImmutableSortedMap.of("k1", "valid2"))));
        assertFalse(matcher.test(createKey(ImmutableSortedMap.of("k1", "invalid"))));
    }

    @Test
    public void test_set_valued_filters() {
        TagMatcher matcher = new TagMatcher(NO_STR_TAGS, ImmutableMap.of("k1", new ContainsAllPredicate(ImmutableSet.of("req1", "req2"))));
        assertFalse(matcher.test(createKey(ImmutableSortedMap.of())));
        assertTrue(matcher.test(createKey(ImmutableSortedMap.of(TagMatcher.MAGIC_TAG, "[\"k1\"]", "k1", "[\"req1\", \"req2\"]"))));
    }
}
