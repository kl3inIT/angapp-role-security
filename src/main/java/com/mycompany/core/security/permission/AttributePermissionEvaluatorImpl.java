package com.mycompany.core.security.permission;

import com.mycompany.core.security.core.SecurityService;
import java.util.Locale;
import org.springframework.stereotype.Service;

/**
 * Default implementation that delegates to {@link RolePermissionService}
 * using "ENTITY.ATTRIBUTE" (upper-cased entity) as permission target.
 */
@Service
public class AttributePermissionEvaluatorImpl implements AttributePermissionEvaluator {

    private final RolePermissionService rolePermissionService;
    private final SecurityService securityService;

    public AttributePermissionEvaluatorImpl(RolePermissionService rolePermissionService, SecurityService securityService) {
        this.rolePermissionService = rolePermissionService;
        this.securityService = securityService;
    }

    @Override
    public boolean canView(Class<?> entityClass, String attribute) {
        return check(entityClass, attribute, AttributeOp.VIEW.name());
    }

    @Override
    public boolean canEdit(Class<?> entityClass, String attribute) {
        return check(entityClass, attribute, AttributeOp.EDIT.name());
    }

    private boolean check(Class<?> entityClass, String attribute, String action) {
        String target = entityClass.getSimpleName().toUpperCase(Locale.ROOT) + "." + attribute.toUpperCase(Locale.ROOT);
        return rolePermissionService.hasPermission(securityService.currentAuthorities(), TargetType.ATTRIBUTE, target, action);
    }
}
