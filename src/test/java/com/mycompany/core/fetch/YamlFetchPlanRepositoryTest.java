package com.mycompany.core.fetch;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.config.ApplicationProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;

class YamlFetchPlanRepositoryTest {

    private YamlFetchPlanRepository repository;

    @BeforeEach
    void setUp() {
        ApplicationProperties applicationProperties = new ApplicationProperties();
        applicationProperties.getFetchPlans().setConfig("classpath:fetch-plans-test.yml");
        repository = new YamlFetchPlanRepository(new DefaultResourceLoader(), applicationProperties);
        repository.loadDefinitions();
    }

    @Test
    void shouldResolveCustomPlanExtendingBase() {
        FetchPlan plan = repository.findByEntityAndCode(TestOrder.class, "summary").orElseThrow();

        assertThat(plan.getScalarAttributes()).contains("id", "number");
        assertThat(plan.getReferences()).containsKeys("customer", "lines");
        assertThat(plan.getReferences().get("customer").getScalarAttributes()).contains("id", "name");
        assertThat(plan.getReferences().get("lines").getScalarAttributes()).contains("id", "quantity");
        assertThat(plan.getReferences().get("lines").getReferences().get("product").getScalarAttributes()).contains("id", "name");
    }

    @Test
    void shouldReturnEmptyWhenPlanIsNotDefined() {
        assertThat(repository.findByEntityAndCode(TestCustomer.class, "missing")).isEmpty();
    }

    @Entity
    static class TestOrder {

        @Id
        private Long id;

        private String number;

        @ManyToOne
        private TestCustomer customer;

        @OneToMany
        private List<TestOrderLine> lines = new ArrayList<>();
    }

    @Entity
    static class TestCustomer {

        @Id
        private Long id;

        private String name;
    }

    @Entity
    static class TestOrderLine {

        @Id
        private Long id;

        private Integer quantity;

        @ManyToOne
        private TestProduct product;
    }

    @Entity
    static class TestProduct {

        @Id
        private Long id;

        private String name;
    }
}
