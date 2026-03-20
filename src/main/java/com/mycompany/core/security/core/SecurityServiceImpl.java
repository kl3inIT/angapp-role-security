package com.mycompany.core.security.core;

import com.mycompany.myapp.security.DomainUserDetailsService;
import com.mycompany.myapp.security.SecurityUtils;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * {@link SecurityService} implementation backed by Spring Security.
 */
@Service
public class SecurityServiceImpl implements SecurityService {

    @Override
    public Collection<String> currentAuthorities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return Collections.emptyList();
        }
        return authentication
            .getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }

    @Override
    public Long currentUserId() {
        // Prefer JWT claim-based id if present, otherwise try UserDetails with id
        return SecurityUtils.getCurrentUserId()
            .or(() -> {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.getPrincipal() instanceof DomainUserDetailsService.UserWithId user) {
                    return java.util.Optional.ofNullable(user.getId());
                }
                return java.util.Optional.empty();
            })
            .orElse(null);
    }

    @Override
    public boolean hasAuthority(String authority) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).anyMatch(a -> Objects.equals(a, authority));
    }
}
