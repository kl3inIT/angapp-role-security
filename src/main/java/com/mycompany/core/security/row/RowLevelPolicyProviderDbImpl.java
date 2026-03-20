package com.mycompany.core.security.row;

import com.mycompany.core.security.core.SecurityService;
import com.mycompany.core.security.domain.SecRowPolicy;
import com.mycompany.core.security.permission.EntityOp;
import com.mycompany.core.security.repository.SecRowPolicyRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 * Row-level policy provider reading policies from {@code sec_row_policy}.
 * <p>
 * For now this implementation supports a very small subset of
 * {@link SecRowPolicy.PolicyType#SPECIFICATION} where {@code expression}
 * is of the form {@code field = CURRENT_USER_ID}.
 */
@Service
@Primary
public class RowLevelPolicyProviderDbImpl implements RowLevelPolicyProvider {

    private final SecRowPolicyRepository rowPolicyRepository;
    private final SecurityService securityService;

    public RowLevelPolicyProviderDbImpl(SecRowPolicyRepository rowPolicyRepository, SecurityService securityService) {
        this.rowPolicyRepository = rowPolicyRepository;
        this.securityService = securityService;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<RowPolicyDefinition<T>> getPolicies(Class<T> entityClass, EntityOp op) {
        List<SecRowPolicy> policies = rowPolicyRepository.findByEntityNameAndOperation(entityClass.getSimpleName(), op);
        List<RowPolicyDefinition<T>> result = new ArrayList<>();

        for (SecRowPolicy p : policies) {
            if (p.getPolicyType() == SecRowPolicy.PolicyType.SPECIFICATION) {
                RowPolicyDefinition<T> def = buildSpecificationPolicy(entityClass, op, p);
                if (def != null) {
                    result.add(def);
                }
            }
        }

        return result;
    }

    private <T> RowPolicyDefinition<T> buildSpecificationPolicy(Class<T> entityClass, EntityOp op, SecRowPolicy p) {
        String expr = p.getExpression();
        // Very small DSL: "field = CURRENT_USER_ID"
        String[] parts = expr.split("=");
        if (parts.length != 2) {
            return null;
        }
        String field = parts[0].trim();
        String valueToken = parts[1].trim();

        if (!"CURRENT_USER_ID".equals(valueToken)) {
            return null;
        }
        Long currentUserId = securityService.currentUserId();
        if (currentUserId == null) {
            return null;
        }

        Specification<T> spec = (root, query, cb) -> cb.equal(root.get(field).get("id"), currentUserId);
        return new RowPolicyDefinition<>(p.getCode(), op, spec);
    }
}
