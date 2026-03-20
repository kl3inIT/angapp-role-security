package com.mycompany.core.security.repository;

import com.mycompany.core.security.domain.SecRowPolicy;
import com.mycompany.core.security.permission.EntityOp;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecRowPolicyRepository extends JpaRepository<SecRowPolicy, Long> {
    List<SecRowPolicy> findByEntityNameAndOperation(String entityName, EntityOp operation);
}
