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
        FetchPlan fp = new FetchPlan();
        fp.setCode(sec.getCode());
        try {
            JsonNode root = objectMapper.readTree(sec.getDefinitionJson());

            // scalarAttributes
            JsonNode scalars = root.path("scalarAttributes");
            if (scalars.isArray()) {
                for (JsonNode n : scalars) {
                    fp.getScalarAttributes().add(n.asText());
                }
            }

            // references
            JsonNode refs = root.path("references");
            if (refs.isObject()) {
                Iterator<Map.Entry<String, JsonNode>> fields = refs.fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> e = fields.next();
                    FetchPlan nested = new FetchPlan();
                    nested.setCode(e.getKey());
                    JsonNode nestedScalars = e.getValue().path("scalarAttributes");
                    if (nestedScalars.isArray()) {
                        for (JsonNode n : nestedScalars) {
                            nested.getScalarAttributes().add(n.asText());
                        }
                    }
                    fp.getReferences().put(e.getKey(), nested);
                }
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid fetch plan JSON for code " + sec.getCode(), e);
        }
        return fp;
    }
}
