package org.kairosdb.core.datastore.setvaluedtags;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.gson.Gson;

import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.Set;

public class ContainsAllPredicate implements SetValuedTagPredicate {
    private static final Gson GSON = new Gson();

    public ContainsAllPredicate(ImmutableSet<String> tags) {
        m_tags = tags;
    }

    @Override
    public boolean matches(Set<String> values) {
        return Sets.difference(m_tags, values).isEmpty();
    }

    @Override
    public String encodeForCacheString() {
        return GSON.toJson(ImmutableMap.of("contains_all", m_tags));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContainsAllPredicate that = (ContainsAllPredicate) o;
        return m_tags.equals(that.m_tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_tags);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("m_tags", m_tags)
                .toString();
    }

    @NotNull
    private ImmutableSet<String> m_tags;
}
