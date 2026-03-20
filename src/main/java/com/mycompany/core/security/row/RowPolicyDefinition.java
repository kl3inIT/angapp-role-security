package com.mycompany.core.security.row;

import com.mycompany.core.security.permission.EntityOp;
import org.springframework.data.jpa.domain.Specification;

/**
 * Definition of a row-level policy for a particular entity and operation.
 */
public class RowPolicyDefinition<T> {

    private final String code;
    private final EntityOp operation;
    private final Specification<T> specification;

    public RowPolicyDefinition(String code, EntityOp operation, Specification<T> specification) {
        this.code = code;
        this.operation = operation;
        this.specification = specification;
    }

    public String getCode() {
        return code;
    }

    public EntityOp getOperation() {
        return operation;
    }

    public Specification<T> getSpecification() {
        return specification;
    }
}
