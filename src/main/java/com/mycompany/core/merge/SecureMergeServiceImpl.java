package com.mycompany.core.merge;

import com.mycompany.core.security.permission.AttributePermissionEvaluator;
import java.util.Map;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

/**
 * Default implementation that merges only attributes the current user is
 * allowed to edit.
 */
@Service
public class SecureMergeServiceImpl implements SecureMergeService {

    private final AttributePermissionEvaluator attributePermissionEvaluator;

    public SecureMergeServiceImpl(AttributePermissionEvaluator attributePermissionEvaluator) {
        this.attributePermissionEvaluator = attributePermissionEvaluator;
    }

    @Override
    public <T> T mergeForUpdate(T entity, Map<String, Object> payload) {
        Class<?> entityClass = entity.getClass();
        BeanWrapper wrapper = new BeanWrapperImpl(entity);

        for (Map.Entry<String, Object> entry : payload.entrySet()) {
            String attr = entry.getKey();

            if (!attributePermissionEvaluator.canEdit(entityClass, attr)) {
                throw new AccessDeniedException("No EDIT permission for " + entityClass.getSimpleName() + "." + attr);
            }

            wrapper.setPropertyValue(attr, entry.getValue());
        }

        return entity;
    }
}
