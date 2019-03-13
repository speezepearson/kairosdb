package org.kairosdb.core.datastore.setvaluedtags;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;

import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.Set;

public class ExactlyPredicate implements SetValuedTagPredicate {
    private static final Gson GSON = new Gson();

    public ExactlyPredicate(ImmutableSet<String> tags) {
        m_tags = tags;
    }

    @Override
    public boolean matches(Set<String> values) {
        return m_tags.equals(values);
    }

    @Override
    public String encodeForCacheString() {
        return GSON.toJson(ImmutableMap.of("contains_all", m_tags));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExactlyPredicate that = (ExactlyPredicate) o;
        return m_tags.equals(that.m_tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_tags);
    }

    @NotNull
    private ImmutableSet<String> m_tags;
}
