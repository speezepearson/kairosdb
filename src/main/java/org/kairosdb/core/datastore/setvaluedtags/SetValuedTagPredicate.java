package org.kairosdb.core.datastore.setvaluedtags;

import javax.annotation.Nullable;
import java.util.Set;

public interface SetValuedTagPredicate {
    boolean matches(@Nullable Set<String> values);
    String encodeForCacheString();
}
