package com.mycompany.core.security.access;

import com.mycompany.core.security.permission.EntityPermissionEvaluator;
import org.springframework.stereotype.Component;

/**
 * Applies CRUD entity permissions to {@link CrudEntityContext}.
 */
@Component
public class CrudEntityConstraint implements AccessConstraint<CrudEntityContext> {

    private final EntityPermissionEvaluator evaluator;

    public CrudEntityConstraint(EntityPermissionEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    @Override
    public Class<CrudEntityContext> supports() {
        return CrudEntityContext.class;
    }

    @Override
    public void applyTo(CrudEntityContext context) {
        context.setPermitted(evaluator.isPermitted(context.getEntityClass(), context.getOperation()));
    }

    @Override
    public int getOrder() {
        return 100;
    }
}
