package com.mycompany.core.security.row;

import com.mycompany.core.security.permission.EntityOp;
import java.util.List;

/**
 * Provides row-level policies for a given entity and operation.
 * <p>
 * Implementations can be static (coded) or dynamic (from DB).
 */
public interface RowLevelPolicyProvider {
    <T> List<RowPolicyDefinition<T>> getPolicies(Class<T> entityClass, EntityOp op);
}
