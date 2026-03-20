package com.mycompany.core.security.permission;

import java.util.Collection;

/**
 * Legacy placeholder implementation kept only as an example.
 * <p>
 * Not annotated as a Spring bean to avoid conflicting with the real
 * database-backed implementation.
 */
public class RolePermissionServiceAllowAllImpl implements RolePermissionService {

    @Override
    public boolean hasPermission(Collection<String> authorities, TargetType targetType, String target, String action) {
        return true;
    }
}
