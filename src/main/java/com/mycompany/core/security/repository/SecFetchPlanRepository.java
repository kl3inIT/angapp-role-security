package com.mycompany.core.security.repository;

import com.mycompany.core.security.domain.SecFetchPlan;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecFetchPlanRepository extends JpaRepository<SecFetchPlan, Long> {
    Optional<SecFetchPlan> findByEntityNameAndCode(String entityName, String code);
}
