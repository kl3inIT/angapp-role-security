package com.mycompany.core.serialize;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.mycompany.core.fetch.FetchPlan;
import com.mycompany.core.security.permission.AttributePermissionEvaluator;
import java.util.Map;
import org.junit.jupiter.api.Test;

class SecureEntitySerializerImplTest {

    @Test
    void shouldAlwaysExposeIdWhenPresentInFetchPlan() {
        AttributePermissionEvaluator attributePermissionEvaluator = mock(AttributePermissionEvaluator.class);
        when(attributePermissionEvaluator.canView(TestEntity.class, "name")).thenReturn(true);
        when(attributePermissionEvaluator.canView(TestEntity.class, "id")).thenReturn(false);

        SecureEntitySerializerImpl serializer = new SecureEntitySerializerImpl(attributePermissionEvaluator);
        FetchPlan fetchPlan = new FetchPlan();
        fetchPlan.getScalarAttributes().add("id");
        fetchPlan.getScalarAttributes().add("name");

        SecureEntityView view = serializer.serialize(new TestEntity(7L, "visible"), fetchPlan);

        assertThat(view.getValues()).isEqualTo(Map.of("id", 7L, "name", "visible"));
    }

    private static final class TestEntity {

        private final Long id;
        private final String name;

        private TestEntity(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}
