package org.kairosdb.core.datastore;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.gson.Gson;

import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.Set;

public class SimpleSetValuedTagPredicate implements SetValuedTagPredicate {
    private static final Gson GSON = new Gson();

    public SimpleSetValuedTagPredicate(ImmutableSet<ImmutableSet<String>> containsAlls,
                                       ImmutableSet<ImmutableSet<String>> containsAnys,
                                       ImmutableSet<ImmutableSet<String>> exactlys) {
        m_containsAlls = containsAlls;
        m_containsAnys = containsAnys;
        m_exactlys = exactlys;
    }

    @Override
    public boolean matches(Set<String> values) {
        for (Set<String> exactly : m_exactlys)
        {
            if (!values.equals(exactly)) return false;
        }
        for (Set<String> containsAll : m_containsAlls)
        {
            if (!Sets.difference(containsAll, values).isEmpty()) return false;
        }
        for (Set<String> containsAny : m_containsAnys)
        {
            if (Sets.intersection(containsAny, values).isEmpty()) return false;
        }
        return true;
    }

    @Override
    public String encodeForCacheString() {
        return GSON.toJson(m_containsAlls);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleSetValuedTagPredicate that = (SimpleSetValuedTagPredicate) o;
        return m_containsAlls.equals(that.m_containsAlls) &&
                m_containsAnys.equals(that.m_containsAnys) &&
                m_exactlys.equals(that.m_exactlys);
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_containsAlls, m_containsAnys, m_exactlys);
    }

    @NotNull
    private ImmutableSet<ImmutableSet<String>> m_containsAlls;
    @NotNull
    private ImmutableSet<ImmutableSet<String>> m_containsAnys;
    @NotNull
    private ImmutableSet<ImmutableSet<String>> m_exactlys;
}
