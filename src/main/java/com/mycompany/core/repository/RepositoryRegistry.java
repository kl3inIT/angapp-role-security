package com.mycompany.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Central registry to obtain Spring Data repositories by entity class.
 * <p>
 * You can implement this interface by wiring known repositories or by
 * using Spring Data's {@code Repositories} helper.
 */
public interface RepositoryRegistry {
    <T> JpaRepository<T, Object> repository(Class<T> entityClass);

    <T> JpaSpecificationExecutor<T> specRepository(Class<T> entityClass);
}
