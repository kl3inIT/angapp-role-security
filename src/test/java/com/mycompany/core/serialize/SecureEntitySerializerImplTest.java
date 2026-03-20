package com.mycompany.core.serialize;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.mycompany.core.fetch.FetchPlan;
import com.mycompany.core.security.permission.AttributePermissionEvaluator;
import java.util.List;
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
        fetchPlan.addProperty("id");
        fetchPlan.addProperty("name");

        SecureEntityView view = serializer.serialize(new TestEntity(7L, "visible"), fetchPlan);

        assertThat(view.getValues()).isEqualTo(Map.of("id", 7L, "name", "visible"));
    }

    @Test
    void shouldSerializeCollectionReferencesUsingNestedFetchPlan() {
        AttributePermissionEvaluator attributePermissionEvaluator = mock(AttributePermissionEvaluator.class);
        when(attributePermissionEvaluator.canView(TestParent.class, "children")).thenReturn(true);
        when(attributePermissionEvaluator.canView(TestChild.class, "name")).thenReturn(true);

        SecureEntitySerializerImpl serializer = new SecureEntitySerializerImpl(attributePermissionEvaluator);
        FetchPlan childPlan = new FetchPlan();
        childPlan.addProperty("name");

        FetchPlan parentPlan = new FetchPlan();
        parentPlan.addProperty("id");
        parentPlan.addProperty("children", childPlan);

        SecureEntityView view = serializer.serialize(new TestParent(11L, List.of(new TestChild("A"), new TestChild("B"))), parentPlan);

        assertThat(view.getValues()).isEqualTo(Map.of("id", 11L, "children", List.of(Map.of("name", "A"), Map.of("name", "B"))));
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

    private static final class TestParent {

        private final Long id;
        private final List<TestChild> children;

        private TestParent(Long id, List<TestChild> children) {
            this.id = id;
            this.children = children;
        }

        public Long getId() {
            return id;
        }

        public List<TestChild> getChildren() {
            return children;
        }
    }

    private static final class TestChild {

        private final String name;

        private TestChild(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
