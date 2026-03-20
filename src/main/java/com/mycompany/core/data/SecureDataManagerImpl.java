package com.mycompany.core.data;

import com.mycompany.core.fetch.FetchPlan;
import com.mycompany.core.fetch.FetchPlanResolver;
import com.mycompany.core.merge.SecureMergeService;
import com.mycompany.core.repository.RepositoryRegistry;
import com.mycompany.core.security.access.AccessManager;
import com.mycompany.core.security.access.CrudEntityContext;
import com.mycompany.core.security.permission.EntityOp;
import com.mycompany.core.security.row.RowLevelSpecificationBuilder;
import com.mycompany.core.serialize.SecureEntitySerializer;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SecureDataManagerImpl implements SecureDataManager {

    private final AccessManager accessManager;
    private final RowLevelSpecificationBuilder rowSpecBuilder;
    private final FetchPlanResolver fetchPlanResolver;
    private final SecureEntitySerializer serializer;
    private final SecureMergeService mergeService;
    private final RepositoryRegistry repositoryRegistry;

    public SecureDataManagerImpl(
        AccessManager accessManager,
        RowLevelSpecificationBuilder rowSpecBuilder,
        FetchPlanResolver fetchPlanResolver,
        SecureEntitySerializer serializer,
        SecureMergeService mergeService,
        RepositoryRegistry repositoryRegistry
    ) {
        this.accessManager = accessManager;
        this.rowSpecBuilder = rowSpecBuilder;
        this.fetchPlanResolver = fetchPlanResolver;
        this.serializer = serializer;
        this.mergeService = mergeService;
        this.repositoryRegistry = repositoryRegistry;
    }

    @Override
    @Transactional(readOnly = true)
    public <T> Map<String, Object> loadOne(Class<T> entityClass, Object id, String fetchPlanCode) {
        checkCrud(entityClass, EntityOp.READ);

        Specification<T> rowSpec = rowSpecBuilder.build(entityClass, EntityOp.READ);
        JpaSpecificationExecutor<T> repo = repositoryRegistry.specRepository(entityClass);

        Specification<T> idSpec = (root, query, cb) -> cb.equal(root.get("id"), id);

        T entity = repo
            .findOne(Specification.where(idSpec).and(rowSpec))
            .orElseThrow(() -> new EntityNotFoundException(entityClass.getSimpleName() + " not found"));

        FetchPlan fetchPlan = fetchPlanResolver.resolve(entityClass, fetchPlanCode);
        return serializer.serialize(entity, fetchPlan).getValues();
    }

    @Override
    @Transactional(readOnly = true)
    public <T> List<Map<String, Object>> loadList(Class<T> entityClass, Specification<T> userSpec, String fetchPlanCode) {
        checkCrud(entityClass, EntityOp.READ);

        Specification<T> rowSpec = rowSpecBuilder.build(entityClass, EntityOp.READ);
        JpaSpecificationExecutor<T> repo = repositoryRegistry.specRepository(entityClass);
        FetchPlan fetchPlan = fetchPlanResolver.resolve(entityClass, fetchPlanCode);

        Specification<T> spec = Specification.where(userSpec).and(rowSpec);

        return repo.findAll(spec).stream().map(entity -> serializer.serialize(entity, fetchPlan).getValues()).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public <T> Page<Map<String, Object>> loadPage(
        Class<T> entityClass,
        Specification<T> userSpec,
        Pageable pageable,
        String fetchPlanCode
    ) {
        checkCrud(entityClass, EntityOp.READ);

        Specification<T> rowSpec = rowSpecBuilder.build(entityClass, EntityOp.READ);
        JpaSpecificationExecutor<T> repo = repositoryRegistry.specRepository(entityClass);
        FetchPlan fetchPlan = fetchPlanResolver.resolve(entityClass, fetchPlanCode);

        Specification<T> spec = Specification.where(userSpec).and(rowSpec);

        return repo.findAll(spec, pageable).map(entity -> serializer.serialize(entity, fetchPlan).getValues());
    }

    @Override
    public <T> Map<String, Object> save(Class<T> entityClass, Object id, Map<String, Object> payload, String fetchPlanCode) {
        checkCrud(entityClass, EntityOp.UPDATE);

        JpaRepository<T, Object> repo = repositoryRegistry.repository(entityClass);
        JpaSpecificationExecutor<T> specRepo = repositoryRegistry.specRepository(entityClass);

        Specification<T> rowSpec = rowSpecBuilder.build(entityClass, EntityOp.UPDATE);
        Specification<T> idSpec = (root, query, cb) -> cb.equal(root.get("id"), id);

        T entity = specRepo
            .findOne(Specification.where(idSpec).and(rowSpec))
            .orElseThrow(() -> new AccessDeniedException("Entity not found or row-level denied"));

        mergeService.mergeForUpdate(entity, payload);
        T saved = repo.save(entity);

        FetchPlan fetchPlan = fetchPlanResolver.resolve(entityClass, fetchPlanCode);
        return serializer.serialize(saved, fetchPlan).getValues();
    }

    @Override
    public <T> void delete(Class<T> entityClass, Object id) {
        checkCrud(entityClass, EntityOp.DELETE);

        JpaRepository<T, Object> repo = repositoryRegistry.repository(entityClass);
        JpaSpecificationExecutor<T> specRepo = repositoryRegistry.specRepository(entityClass);

        Specification<T> rowSpec = rowSpecBuilder.build(entityClass, EntityOp.DELETE);
        Specification<T> idSpec = (root, query, cb) -> cb.equal(root.get("id"), id);

        T entity = specRepo
            .findOne(Specification.where(idSpec).and(rowSpec))
            .orElseThrow(() -> new AccessDeniedException("Entity not found or row-level denied"));

        repo.delete(entity);
    }

    private <T> void checkCrud(Class<T> entityClass, EntityOp op) {
        CrudEntityContext context = accessManager.applyRegisteredConstraints(new CrudEntityContext(entityClass, op));

        if (!context.isPermitted()) {
            throw new AccessDeniedException("No " + op + " permission for " + entityClass.getSimpleName());
        }
    }
}
