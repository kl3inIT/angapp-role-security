package com.mycompany.core.fetch;

import com.mycompany.core.security.access.AccessManager;
import com.mycompany.core.security.access.FetchPlanAccessContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

/**
 * Default implementation that checks fetch-plan permissions via {@link AccessManager}
 * and then uses {@link FetchPlanRepository} to obtain the plan definition.
 */
@Service
public class FetchPlanResolverImpl implements FetchPlanResolver {

    private final FetchPlanRepository fetchPlanRepository;
    private final AccessManager accessManager;

    public FetchPlanResolverImpl(FetchPlanRepository fetchPlanRepository, AccessManager accessManager) {
        this.fetchPlanRepository = fetchPlanRepository;
        this.accessManager = accessManager;
    }

    @Override
    public <T> FetchPlan resolve(Class<T> entityClass, String planCode) {
        FetchPlanAccessContext context = accessManager.applyRegisteredConstraints(new FetchPlanAccessContext(entityClass, planCode));

        if (!context.isPermitted()) {
            throw new AccessDeniedException("Fetch plan not allowed: " + planCode);
        }

        return fetchPlanRepository
            .findByEntityAndCode(entityClass, planCode)
            .orElseThrow(() -> new IllegalArgumentException("Fetch plan not found: " + planCode));
    }
}
