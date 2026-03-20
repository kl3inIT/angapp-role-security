package com.mycompany.core.security.permission;

import java.util.Collection;

/**
 * Abstraction for dynamic permission lookup for the current role model.
 * <p>
 * You can implement this interface using your own DB schema
 * (e.g. sec_role, sec_permission, ...) or any external authorization source.
 */
public interface RolePermissionService {
    /**
     * Checks if any of the given authorities has the specified permission.
     *
     * @param authorities authorities of the current user (e.g. Spring Security roles)
     * @param targetType  type of secured resource
     * @param target      logical target code (e.g. ORDER, ORDER.totalAmount)
     * @param action      action on the target (e.g. READ, EDIT, APPLY)
     */
    boolean hasPermission(Collection<String> authorities, TargetType targetType, String target, String action);
}
