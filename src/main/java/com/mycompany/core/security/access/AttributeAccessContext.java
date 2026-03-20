package com.mycompany.core.security.access;

import com.mycompany.core.security.permission.AttributeOp;

/**
 * Access context for attribute-level checks on an entity.
 */
public class AttributeAccessContext implements AccessContext {

    private final Class<?> entityClass;
    private final String attribute;
    private final AttributeOp operation;
    private boolean permitted;

    public AttributeAccessContext(Class<?> entityClass, String attribute, AttributeOp operation) {
        this.entityClass = entityClass;
        this.attribute = attribute;
        this.operation = operation;
        this.permitted = false;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public String getAttribute() {
        return attribute;
    }

    public AttributeOp getOperation() {
        return operation;
    }

    public boolean isPermitted() {
        return permitted;
    }

    public void setPermitted(boolean permitted) {
        this.permitted = permitted;
    }
}
