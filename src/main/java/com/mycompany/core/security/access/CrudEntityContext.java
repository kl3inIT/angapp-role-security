package com.mycompany.core.security.access;

import com.mycompany.core.security.permission.EntityOp;

/**
 * Access context for CRUD-level checks on an entity.
 */
public class CrudEntityContext implements AccessContext {

    private final Class<?> entityClass;
    private final EntityOp operation;
    private boolean permitted;

    public CrudEntityContext(Class<?> entityClass, EntityOp operation) {
        this.entityClass = entityClass;
        this.operation = operation;
        this.permitted = false;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public EntityOp getOperation() {
        return operation;
    }

    public boolean isPermitted() {
        return permitted;
    }

    public void setPermitted(boolean permitted) {
        this.permitted = permitted;
    }
}
