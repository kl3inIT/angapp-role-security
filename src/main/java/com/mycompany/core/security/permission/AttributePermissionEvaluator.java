package com.mycompany.core.security.permission;

/**
 * Evaluates view/edit permissions on entity attributes.
 */
public interface AttributePermissionEvaluator {
    boolean canView(Class<?> entityClass, String attribute);

    boolean canEdit(Class<?> entityClass, String attribute);
}
