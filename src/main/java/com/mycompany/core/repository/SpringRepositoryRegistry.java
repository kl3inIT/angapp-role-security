package com.mycompany.core.repository;

import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Component;

/**
 * {@link RepositoryRegistry} implementation based on Spring Data's
 * {@link Repositories} helper.
 */
@Component
public class SpringRepositoryRegistry implements RepositoryRegistry {

    private final Repositories repositories;

    public SpringRepositoryRegistry(ApplicationContext applicationContext) {
        this.repositories = new Repositories(applicationContext);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> JpaRepository<T, Object> repository(Class<T> entityClass) {
        Object repo = repositories
            .getRepositoryFor(entityClass)
            .orElseThrow(() -> new IllegalArgumentException("No JpaRepository found for " + entityClass.getName()));
        if (!(repo instanceof JpaRepository<?, ?> jpaRepo)) {
            throw new IllegalStateException("Repository for " + entityClass.getName() + " is not a JpaRepository");
        }
        return (JpaRepository<T, Object>) jpaRepo;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> JpaSpecificationExecutor<T> specRepository(Class<T> entityClass) {
        Object repo = repositories
            .getRepositoryFor(entityClass)
            .orElseThrow(() -> new IllegalArgumentException("No JpaSpecificationExecutor found for " + entityClass.getName()));
        if (!(repo instanceof JpaSpecificationExecutor<?> specRepo)) {
            throw new IllegalStateException("Repository for " + entityClass.getName() + " is not a JpaSpecificationExecutor");
        }
        return (JpaSpecificationExecutor<T>) specRepo;
    }
}
