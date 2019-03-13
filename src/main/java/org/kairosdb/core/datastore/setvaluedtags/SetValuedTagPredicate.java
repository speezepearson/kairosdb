package org.kairosdb.core.datastore.setvaluedtags;

import java.util.Set;

public interface SetValuedTagPredicate {
    boolean matches(Set<String> values);
    String encodeForCacheString();
}
