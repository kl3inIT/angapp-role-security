package com.mycompany.core.security.core;

import java.util.Collection;

/**
 * Abstraction over the underlying security context (typically Spring Security).
 * Implementations should adapt from {@code Authentication} / JWT to simple
 * primitives used by the security engine.
 */
public interface SecurityService {
    /**
     * @return the current user's authorities (e.g. role codes).
     */
    Collection<String> currentAuthorities();

    /**
     * @return the current user's identifier if available, otherwise {@code null}.
     */
    Long currentUserId();

    /**
     * Convenience method to check for a single authority.
     */
    boolean hasAuthority(String authority);
}
