package com.mycompany.core.fetch;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Simplified Jmix-like fetch plan model backed by named properties.
 */
public class FetchPlan {

    private String code;
    private Class<?> entityClass;
    private final Map<String, FetchPlanProperty> properties = new LinkedHashMap<>();

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

    public Collection<FetchPlanProperty> getProperties() {
        return properties.values();
    }

    public Map<String, FetchPlanProperty> getPropertiesMap() {
        return properties;
    }

    public FetchPlanProperty getProperty(String name) {
        return properties.get(name);
    }

    public boolean containsProperty(String name) {
        return properties.containsKey(name);
    }

    public void addProperty(String name) {
        properties.put(name, new FetchPlanProperty(name, null));
    }

    public void addProperty(String name, FetchPlan nestedFetchPlan) {
        properties.put(name, new FetchPlanProperty(name, nestedFetchPlan));
    }
}
