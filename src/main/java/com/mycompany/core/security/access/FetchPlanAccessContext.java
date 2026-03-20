package com.mycompany.core.security.access;

/**
 * Access context for applying a particular fetch plan on an entity.
 */
public class FetchPlanAccessContext implements AccessContext {

    private final Class<?> entityClass;
    private final String planCode;
    private boolean permitted = true;

    public FetchPlanAccessContext(Class<?> entityClass, String planCode) {
        this.entityClass = entityClass;
        this.planCode = planCode;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public String getPlanCode() {
        return planCode;
    }

    public boolean isPermitted() {
        return permitted;
    }

    public void setPermitted(boolean permitted) {
        this.permitted = permitted;
    }
}
