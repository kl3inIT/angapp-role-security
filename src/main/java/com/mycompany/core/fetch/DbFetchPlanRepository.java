package com.mycompany.core.fetch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.core.security.domain.SecFetchPlan;
import com.mycompany.core.security.repository.SecFetchPlanRepository;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * {@link FetchPlanRepository} implementation backed by {@link SecFetchPlan}
 * table containing JSON definitions.
 */
@Repository
public class DbFetchPlanRepository implements FetchPlanRepository {

    private final SecFetchPlanRepository secFetchPlanRepository;
    private final ObjectMapper objectMapper;

    public DbFetchPlanRepository(SecFetchPlanRepository secFetchPlanRepository, ObjectMapper objectMapper) {
        this.secFetchPlanRepository = secFetchPlanRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public <T> Optional<FetchPlan> findByEntityAndCode(Class<T> entityClass, String code) {
        return secFetchPlanRepository.findByEntityNameAndCode(entityClass.getSimpleName(), code).map(this::toFetchPlan);
    }

    private FetchPlan toFetchPlan(SecFetchPlan sec) {
        FetchPlan fetchPlan = new FetchPlan();
        fetchPlan.setCode(sec.getCode());
        try {
            JsonNode root = objectMapper.readTree(sec.getDefinitionJson());
            populateFetchPlan(fetchPlan, root);
            return fetchPlan;
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid fetch plan JSON for code " + sec.getCode(), e);
        }
    }

    private void populateFetchPlan(FetchPlan fetchPlan, JsonNode root) {
        JsonNode scalars = root.path("scalarAttributes");
        if (scalars.isArray()) {
            for (JsonNode scalar : scalars) {
                fetchPlan.addProperty(scalar.asText());
            }
        }

        JsonNode references = root.path("references");
        if (!references.isObject()) {
            return;
        }

        Iterator<Map.Entry<String, JsonNode>> fields = references.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            FetchPlan nested = new FetchPlan();
            nested.setCode(entry.getKey());
            populateFetchPlan(nested, entry.getValue());
            fetchPlan.addProperty(entry.getKey(), nested);
        }
    }
}
