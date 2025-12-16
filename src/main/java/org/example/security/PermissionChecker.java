package org.example.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class PermissionChecker {

    public void require(String authority) {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getAuthorities() == null) {
            throw new AccessDeniedException("Not authenticated");
        }

        boolean allowed = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(authority));

        if (!allowed) {
            throw new AccessDeniedException("Forbidden: missing authority " + authority);
        }
    }
}