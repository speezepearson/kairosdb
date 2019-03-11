package org.kairosdb.core.datastore;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class SimpleSetValuedTagPredicate implements SetValuedTagPredicate {
    private static final Gson GSON = new Gson();

    public SimpleSetValuedTagPredicate(TreeSet<String> containsAll) {
        this.containsAll = containsAll;
    }

    @Override
    public boolean matches(Set<String> values) {
        return Sets.difference(this.containsAll, values).isEmpty();
    }

    @Override
    public String encodeForCacheString() {
        return GSON.toJson(this.containsAll);
    }

    @NotNull
    @SerializedName("contains_all")
    private TreeSet<String> containsAll;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleSetValuedTagPredicate that = (SimpleSetValuedTagPredicate) o;
        return containsAll.equals(that.containsAll);
    }

    @Override
    public int hashCode() {
        return Objects.hash(containsAll);
    }
}
