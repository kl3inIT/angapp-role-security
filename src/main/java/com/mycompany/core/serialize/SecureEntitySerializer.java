package com.mycompany.core.serialize;

import com.mycompany.core.fetch.FetchPlan;

public interface SecureEntitySerializer {
    SecureEntityView serialize(Object entity, FetchPlan fetchPlan);
}
