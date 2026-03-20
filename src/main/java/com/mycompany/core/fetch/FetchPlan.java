package com.mycompany.core.fetch;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Simple representation of a fetch plan for an entity.
 * <p>
 * It defines which scalar attributes and referenced objects should be loaded.
 */
public class FetchPlan {

    private String code;
    private Class<?> entityClass;
    private Set<String> scalarAttributes = new LinkedHashSet<>();
    private Map<String, FetchPlan> references = new LinkedHashMap<>();

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    public Set<String> getScalarAttributes() {
        return scalarAttributes;
    }

    public void setScalarAttributes(Set<String> scalarAttributes) {
        this.scalarAttributes = scalarAttributes;
    }

    public Map<String, FetchPlan> getReferences() {
        return references;
    }

    public void setReferences(Map<String, FetchPlan> references) {
        this.references = references;
    }
}
