package com.mycompany.core.fetch;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

/**
 * Simple in-memory {@link FetchPlanRepository} that can be populated at
 * startup or through configuration beans.
 * <p>
 * For production you will typically replace this with an implementation
 * backed by database metadata.
 */
@Repository
public class InMemoryFetchPlanRepository implements FetchPlanRepository {

    private final Map<String, FetchPlan> plans = new ConcurrentHashMap<>();

    @Override
    public <T> Optional<FetchPlan> findByEntityAndCode(Class<T> entityClass, String code) {
        String key = buildKey(entityClass, code);
        return Optional.ofNullable(plans.get(key));
    }

    public <T> void register(Class<T> entityClass, FetchPlan plan) {
        plans.put(buildKey(entityClass, plan.getCode()), plan);
    }

    private String buildKey(Class<?> entityClass, String code) {
        return entityClass.getName() + "#" + code;
    }
}
