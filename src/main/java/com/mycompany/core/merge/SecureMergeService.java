package com.mycompany.core.merge;

import java.util.Map;

/**
 * Applies an update payload to an existing entity instance, enforcing
 * attribute-level write permissions.
 */
public interface SecureMergeService {
    <T> T mergeForUpdate(T entity, Map<String, Object> payload);
}
