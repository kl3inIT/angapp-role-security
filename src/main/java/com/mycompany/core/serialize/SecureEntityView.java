package com.mycompany.core.serialize;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Generic, map-based view of an entity after security filtering.
 */
public class SecureEntityView {

    private final Class<?> entityClass;
    private final Map<String, Object> values = new LinkedHashMap<>();

    public SecureEntityView(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public Map<String, Object> getValues() {
        return values;
    }

    public void put(String name, Object value) {
        values.put(name, value);
    }
}
