package com.mycompany.core.security.permission;

/**
 * Evaluates CRUD permissions on entities.
 */
public interface EntityPermissionEvaluator {
    boolean isPermitted(Class<?> entityClass, EntityOp op);
}
