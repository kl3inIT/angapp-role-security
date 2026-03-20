package com.mycompany.core.fetch;

/**
 * Resolves a fetch plan and enforces security checks.
 */
public interface FetchPlanResolver {
    <T> FetchPlan resolve(Class<T> entityClass, String planCode);
}
