package com.mycompany.core.security.access;

/**
 * Applies registered {@link AccessConstraint}s to access contexts.
 */
public interface AccessManager {
    <C extends AccessContext> C applyRegisteredConstraints(C context);
}
