package com.mycompany.core.security.permission;

import com.mycompany.core.security.core.SecurityService;
import org.springframework.stereotype.Service;

/**
 * Default implementation that delegates to {@link RolePermissionService}
 * using the entity simple name (upper-cased) as target.
 */
@Service
public class EntityPermissionEvaluatorImpl implements EntityPermissionEvaluator {

    private final RolePermissionService rolePermissionService;
    private final SecurityService securityService;

    public EntityPermissionEvaluatorImpl(RolePermissionService rolePermissionService, SecurityService securityService) {
        this.rolePermissionService = rolePermissionService;
        this.securityService = securityService;
    }

    @Override
    public boolean isPermitted(Class<?> entityClass, EntityOp op) {
        String target = entityClass.getSimpleName().toUpperCase();
        return rolePermissionService.hasPermission(securityService.currentAuthorities(), TargetType.ENTITY, target, op.name());
    }
}
