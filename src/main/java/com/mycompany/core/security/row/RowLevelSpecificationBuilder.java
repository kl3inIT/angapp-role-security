package com.mycompany.core.security.row;

import com.mycompany.core.security.permission.EntityOp;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 * Builds a combined {@link Specification} from all applicable row-level policies.
 */
@Service
public class RowLevelSpecificationBuilder {

    private final RowLevelPolicyProvider policyProvider;

    public RowLevelSpecificationBuilder(RowLevelPolicyProvider policyProvider) {
        this.policyProvider = policyProvider;
    }

    public <T> Specification<T> build(Class<T> entityClass, EntityOp op) {
        List<RowPolicyDefinition<T>> policies = policyProvider.getPolicies(entityClass, op);

        Specification<T> result = Specification.where(null);
        for (RowPolicyDefinition<T> policy : policies) {
            result = result.and(policy.getSpecification());
        }
        return result;
    }
}
