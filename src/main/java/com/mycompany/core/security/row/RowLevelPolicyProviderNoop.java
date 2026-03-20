package com.mycompany.core.security.row;

import com.mycompany.core.security.permission.EntityOp;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Default no-op implementation that provides no row-level policies.
 * <p>
 * Replace or complement this with implementations that read policies from
 * configuration or database.
 */
@Service
public class RowLevelPolicyProviderNoop implements RowLevelPolicyProvider {

    @Override
    public <T> List<RowPolicyDefinition<T>> getPolicies(Class<T> entityClass, EntityOp op) {
        return List.of();
    }
}
