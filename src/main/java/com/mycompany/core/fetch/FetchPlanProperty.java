package com.mycompany.core.fetch;

import java.util.Objects;

/**
 * Defines a fetch plan property. If {@code fetchPlan} is null, the property is
 * treated as a scalar/local attribute. Otherwise it is a nested reference.
 */
public class FetchPlanProperty {

    private final String name;
    private final FetchPlan fetchPlan;

    public FetchPlanProperty(String name, FetchPlan fetchPlan) {
        this.name = name;
        this.fetchPlan = fetchPlan;
    }

    public String getName() {
        return name;
    }

    public FetchPlan getFetchPlan() {
        return fetchPlan;
    }

    public boolean isReference() {
        return fetchPlan != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FetchPlanProperty)) {
            return false;
        }
        FetchPlanProperty that = (FetchPlanProperty) o;
        return Objects.equals(name, that.name) && Objects.equals(fetchPlan, that.fetchPlan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, fetchPlan);
    }
}
