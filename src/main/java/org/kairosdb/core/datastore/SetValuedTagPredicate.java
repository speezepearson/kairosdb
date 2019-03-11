package org.kairosdb.core.datastore;

import java.util.Set;

public interface SetValuedTagPredicate {
    boolean matches(Set<String> values);
    String encodeForCacheString();
}
