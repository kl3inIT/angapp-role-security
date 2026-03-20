package com.mycompany.core.fetch;

import java.util.Optional;

/**
 * Abstraction for resolving {@link FetchPlan} definitions, e.g. from JSON
 * in the database or from static configuration.
 */
public interface FetchPlanRepository {
    <T> Optional<FetchPlan> findByEntityAndCode(Class<T> entityClass, String code);
}
