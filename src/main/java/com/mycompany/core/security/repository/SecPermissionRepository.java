package com.mycompany.core.security.repository;

import com.mycompany.core.security.domain.SecPermission;
import com.mycompany.core.security.permission.TargetType;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SecPermissionRepository extends JpaRepository<SecPermission, Long> {
    @Query(
        "select p from SecPermission p " +
        "where p.role.code in :roleCodes " +
        "and p.targetType = :targetType " +
        "and p.target = :target " +
        "and p.action = :action"
    )
    List<SecPermission> findByRolesAndTarget(
        @Param("roleCodes") Collection<String> roleCodes,
        @Param("targetType") TargetType targetType,
        @Param("target") String target,
        @Param("action") String action
    );
}
