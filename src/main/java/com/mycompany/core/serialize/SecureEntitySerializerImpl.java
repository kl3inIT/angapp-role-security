package com.mycompany.core.serialize;

import com.mycompany.core.fetch.FetchPlan;
import com.mycompany.core.security.permission.AttributePermissionEvaluator;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;

/**
 * Serializes an entity into a {@link SecureEntityView} honoring attribute
 * permissions and fetch plan definitions.
 */
@Service
public class SecureEntitySerializerImpl implements SecureEntitySerializer {

    private final AttributePermissionEvaluator attributePermissionEvaluator;

    public SecureEntitySerializerImpl(AttributePermissionEvaluator attributePermissionEvaluator) {
        this.attributePermissionEvaluator = attributePermissionEvaluator;
    }

    @Override
    public SecureEntityView serialize(Object entity, FetchPlan fetchPlan) {
        Class<?> entityClass = entity.getClass();
        SecureEntityView view = new SecureEntityView(entityClass);

        BeanWrapper wrapper = new BeanWrapperImpl(entity);

        for (String attr : fetchPlan.getScalarAttributes()) {
            if (isAlwaysVisible(attr) || attributePermissionEvaluator.canView(entityClass, attr)) {
                view.put(attr, wrapper.getPropertyValue(attr));
            }
        }

        for (Map.Entry<String, FetchPlan> ref : fetchPlan.getReferences().entrySet()) {
            String attr = ref.getKey();

            if (!attributePermissionEvaluator.canView(entityClass, attr)) {
                continue;
            }

            Object refValue = wrapper.getPropertyValue(attr);
            if (refValue == null) {
                view.put(attr, null);
            } else if (refValue instanceof Collection<?> collection) {
                view.put(attr, collection.stream().map(item -> serialize(item, ref.getValue()).getValues()).toList());
            } else if (refValue.getClass().isArray()) {
                view.put(attr, serializeArray(refValue, ref.getValue()));
            } else {
                view.put(attr, serialize(refValue, ref.getValue()).getValues());
            }
        }

        return view;
    }

    private boolean isAlwaysVisible(String attr) {
        return "id".equals(attr.toLowerCase(Locale.ROOT));
    }

    private Collection<Map<String, Object>> serializeArray(Object array, FetchPlan fetchPlan) {
        int length = Array.getLength(array);
        Collection<Map<String, Object>> values = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            values.add(serialize(Array.get(array, i), fetchPlan).getValues());
        }
        return values;
    }
}
