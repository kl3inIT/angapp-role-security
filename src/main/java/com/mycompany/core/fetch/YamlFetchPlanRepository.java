package com.mycompany.core.fetch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.mycompany.myapp.config.ApplicationProperties;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Repository;

/**
 * Shared fetch plan repository backed by a classpath YAML file.
 * Supports named plans, built-in plans and basic Jmix-like {@code extends}.
 */
@Repository
public class YamlFetchPlanRepository implements FetchPlanRepository {

    private static final String FETCH_PLANS = "fetch-plans";
    private static final String FETCH_PLANS_CAMEL = "fetchPlans";

    private final ResourceLoader resourceLoader;
    private final ApplicationProperties applicationProperties;
    private final ObjectMapper yamlObjectMapper = new ObjectMapper(new YAMLFactory());
    private final Map<String, JsonNode> definitions = new ConcurrentHashMap<>();

    public YamlFetchPlanRepository(ResourceLoader resourceLoader, ApplicationProperties applicationProperties) {
        this.resourceLoader = resourceLoader;
        this.applicationProperties = applicationProperties;
    }

    @PostConstruct
    void loadDefinitions() {
        definitions.clear();

        Resource resource = resourceLoader.getResource(applicationProperties.getFetchPlans().getConfig());
        if (!resource.exists()) {
            return;
        }

        try (InputStream inputStream = resource.getInputStream()) {
            JsonNode root = yamlObjectMapper.readTree(inputStream);
            JsonNode plans = root.path(FETCH_PLANS);
            if (plans.isMissingNode()) {
                plans = root.path(FETCH_PLANS_CAMEL);
            }

            if (!plans.isArray()) {
                return;
            }

            for (JsonNode planNode : plans) {
                String entity = requiredText(planNode, "entity");
                String name = requiredText(planNode, "name");
                definitions.put(buildKey(entity, name), planNode.deepCopy());
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read fetch plans YAML", e);
        }
    }

    @Override
    public <T> Optional<FetchPlan> findByEntityAndCode(Class<T> entityClass, String code) {
        try {
            return Optional.of(resolve(entityClass, code, new LinkedHashSet<>()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private <T> FetchPlan resolve(Class<T> entityClass, String code, Set<String> resolvingStack) {
        JsonNode definition = definitions.get(buildKey(entityClass.getName(), code));
        if (definition == null) {
            definition = definitions.get(buildKey(entityClass.getSimpleName(), code));
        }
        if (definition == null) {
            throw new IllegalArgumentException("Fetch plan not found in YAML: " + entityClass.getName() + "#" + code);
        }

        String resolutionKey = buildKey(entityClass.getName(), code);
        if (!resolvingStack.add(resolutionKey)) {
            throw new IllegalArgumentException("Cyclic fetch plan inheritance detected: " + resolvingStack);
        }

        try {
            FetchPlan plan = new FetchPlan();
            plan.setCode(code);
            plan.setEntityClass(entityClass);

            String extendsCode = text(definition, "extends");
            if (extendsCode != null && !extendsCode.isBlank()) {
                mergeInto(plan, resolve(entityClass, extendsCode, resolvingStack));
            }

            applyProperties(entityClass, plan, definition.path("properties"), resolvingStack);
            return plan;
        } finally {
            resolvingStack.remove(resolutionKey);
        }
    }

    private <T> void applyProperties(Class<T> entityClass, FetchPlan plan, JsonNode propertiesNode, Set<String> resolvingStack) {
        if (!propertiesNode.isArray()) {
            return;
        }

        for (JsonNode propertyNode : propertiesNode) {
            if (propertyNode.isTextual()) {
                applyPropertyDefinition(entityClass, plan, propertyNode.asText(), null, resolvingStack);
            } else if (propertyNode.isObject()) {
                String propertyName = requiredText(propertyNode, "name");
                applyPropertyDefinition(entityClass, plan, propertyName, propertyNode, resolvingStack);
            }
        }
    }

    private <T> void applyPropertyDefinition(
        Class<T> entityClass,
        FetchPlan plan,
        String propertyName,
        JsonNode propertyNode,
        Set<String> resolvingStack
    ) {
        PropertyMetadata metadata = describeProperty(entityClass, propertyName);
        if (!metadata.reference()) {
            plan.getScalarAttributes().add(propertyName);
            return;
        }

        Class<?> targetType = metadata.propertyType();
        String nestedPlanName = propertyNode == null ? null : text(propertyNode, "fetchPlan");
        JsonNode nestedProperties = propertyNode == null ? null : propertyNode.path("properties");

        FetchPlan nestedPlan;
        if (nestedPlanName != null && !nestedPlanName.isBlank()) {
            nestedPlan = resolve(targetType, nestedPlanName, resolvingStack);
        } else if (nestedProperties != null && nestedProperties.isArray()) {
            nestedPlan = new FetchPlan();
            nestedPlan.setCode(propertyName + "-inline");
            nestedPlan.setEntityClass(targetType);
            applyProperties(targetType, nestedPlan, nestedProperties, resolvingStack);
        } else {
            throw new IllegalArgumentException(
                "Reference property " + entityClass.getName() + "." + propertyName + " requires explicit fetchPlan or nested properties"
            );
        }

        plan.getReferences().put(propertyName, nestedPlan);
    }

    private void mergeInto(FetchPlan target, FetchPlan source) {
        target.getScalarAttributes().addAll(source.getScalarAttributes());

        source
            .getReferences()
            .forEach((name, nestedSource) -> {
                FetchPlan existing = target.getReferences().get(name);
                if (existing == null) {
                    target.getReferences().put(name, copyOf(nestedSource));
                    return;
                }

                mergeInto(existing, nestedSource);
            });
    }

    private FetchPlan copyOf(FetchPlan source) {
        FetchPlan copy = new FetchPlan();
        copy.setCode(source.getCode());
        copy.setEntityClass(source.getEntityClass());
        copy.getScalarAttributes().addAll(source.getScalarAttributes());
        source.getReferences().forEach((name, nested) -> copy.getReferences().put(name, copyOf(nested)));
        return copy;
    }

    private PropertyMetadata describeProperty(Class<?> entityClass, String propertyName) {
        Field field = findField(entityClass, propertyName);
        if (field != null) {
            if (isReferenceField(field)) {
                return new PropertyMetadata(resolvePropertyType(field), true);
            }
            return new PropertyMetadata(resolvePropertyType(field), false);
        }

        try {
            for (PropertyDescriptor descriptor : Introspector.getBeanInfo(entityClass).getPropertyDescriptors()) {
                if (propertyName.equals(descriptor.getName()) && descriptor.getPropertyType() != null) {
                    return new PropertyMetadata(descriptor.getPropertyType(), !BeanUtils.isSimpleValueType(descriptor.getPropertyType()));
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to inspect property " + entityClass.getName() + "." + propertyName, e);
        }

        throw new IllegalArgumentException("Unknown property " + entityClass.getName() + "." + propertyName);
    }

    private boolean isReferenceField(Field field) {
        return (
            field.isAnnotationPresent(OneToOne.class) ||
            field.isAnnotationPresent(OneToMany.class) ||
            field.isAnnotationPresent(ManyToOne.class) ||
            field.isAnnotationPresent(ManyToMany.class) ||
            field.isAnnotationPresent(Embedded.class) ||
            field.isAnnotationPresent(EmbeddedId.class)
        );
    }

    private Class<?> resolvePropertyType(Field field) {
        if (Iterable.class.isAssignableFrom(field.getType())) {
            Type genericType = field.getGenericType();
            if (genericType instanceof ParameterizedType parameterizedType) {
                Type argument = parameterizedType.getActualTypeArguments()[0];
                if (argument instanceof Class<?> argumentClass) {
                    return argumentClass;
                }
            }
        }
        return field.getType();
    }

    private Field findField(Class<?> entityClass, String propertyName) {
        Class<?> current = entityClass;
        while (current != null && current != Object.class) {
            try {
                return current.getDeclaredField(propertyName);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        return null;
    }

    private String requiredText(JsonNode node, String name) {
        String value = text(node, name);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing required fetch plan field: " + name);
        }
        return value;
    }

    private String text(JsonNode node, String name) {
        JsonNode value = node.path(name);
        return value.isMissingNode() || value.isNull() ? null : value.asText();
    }

    private String buildKey(String entity, String code) {
        return entity.toLowerCase(Locale.ROOT) + "#" + code;
    }

    private record PropertyMetadata(Class<?> propertyType, boolean reference) {}
}
