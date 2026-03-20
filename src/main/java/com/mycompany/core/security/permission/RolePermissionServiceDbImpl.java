package com.mycompany.core.security.permission;

import com.mycompany.core.security.domain.SecPermission;
import com.mycompany.core.security.repository.SecPermissionRepository;
import java.util.Collection;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@link RolePermissionService} implementation backed by {@code sec_role}
 * and {@code sec_permission} tables.
 */
@Service
@Transactional(readOnly = true)
public class RolePermissionServiceDbImpl implements RolePermissionService {

    private final SecPermissionRepository permissionRepository;

    public RolePermissionServiceDbImpl(SecPermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    public boolean hasPermission(Collection<String> authorities, TargetType targetType, String target, String action) {
        if (authorities == null || authorities.isEmpty()) {
            return false;
        }

        List<SecPermission> perms = permissionRepository.findByRolesAndTarget(authorities, targetType, target, action);

        boolean hasDeny = perms.stream().anyMatch(p -> p.getEffect() == SecPermission.Effect.DENY);
        if (hasDeny) {
            return false;
        }

        return perms.stream().anyMatch(p -> p.getEffect() == SecPermission.Effect.ALLOW);
    }
}
