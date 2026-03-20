package com.mycompany.core.fetch;

import java.util.Optional;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

/**
 * Resolves fetch plans from shared YAML configuration first, then falls back
 * to database-backed metadata.
 */
@Repository
@Primary
public class CompositeFetchPlanRepository implements FetchPlanRepository {

    private final YamlFetchPlanRepository yamlFetchPlanRepository;
    private final DbFetchPlanRepository dbFetchPlanRepository;

    public CompositeFetchPlanRepository(YamlFetchPlanRepository yamlFetchPlanRepository, DbFetchPlanRepository dbFetchPlanRepository) {
        this.yamlFetchPlanRepository = yamlFetchPlanRepository;
        this.dbFetchPlanRepository = dbFetchPlanRepository;
    }

    @Override
    public <T> Optional<FetchPlan> findByEntityAndCode(Class<T> entityClass, String code) {
        return yamlFetchPlanRepository
            .findByEntityAndCode(entityClass, code)
            .or(() -> dbFetchPlanRepository.findByEntityAndCode(entityClass, code));
    }
}
