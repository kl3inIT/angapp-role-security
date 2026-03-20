package com.mycompany.core.security.access;

import com.mycompany.core.security.permission.EntityOp;
import org.springframework.data.jpa.domain.Specification;

/**
 * Access context for row-level constraints expressed as {@link Specification}.
 */
public class RowLevelAccessContext<T> implements AccessContext {

    private final Class<T> entityClass;
    private final EntityOp operation;
    private Specification<T> specification;

    public RowLevelAccessContext(Class<T> entityClass, EntityOp operation) {
        this.entityClass = entityClass;
        this.operation = operation;
        this.specification = Specification.where(null);
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public EntityOp getOperation() {
        return operation;
    }

    public Specification<T> getSpecification() {
        return specification;
    }

    public void and(Specification<T> spec) {
        this.specification = this.specification.and(spec);
    }
}
