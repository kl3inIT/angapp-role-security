package com.mycompany.core.security.repository;

import com.mycompany.core.security.domain.SecRole;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecRoleRepository extends JpaRepository<SecRole, Long> {
    Optional<SecRole> findByCode(String code);
}
