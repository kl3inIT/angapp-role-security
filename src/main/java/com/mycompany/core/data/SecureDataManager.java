package com.mycompany.core.data;

import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * Central entry point for secured data access, inspired by Jmix DataManager.
 */
public interface SecureDataManager {
    <T> Map<String, Object> loadOne(Class<T> entityClass, Object id, String fetchPlanCode);

    <T> List<Map<String, Object>> loadList(Class<T> entityClass, Specification<T> userSpec, String fetchPlanCode);

    <T> Page<Map<String, Object>> loadPage(Class<T> entityClass, Specification<T> userSpec, Pageable pageable, String fetchPlanCode);

    <T> Map<String, Object> save(Class<T> entityClass, Object id, Map<String, Object> payload, String fetchPlanCode);

    <T> void delete(Class<T> entityClass, Object id);
}
