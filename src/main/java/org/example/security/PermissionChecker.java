package org.example.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PermissionChecker {
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.debug("No authenticated user found in SecurityContext");
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            log.debug("Current authenticated user: {}", username);
            return username;
        } else if (principal instanceof String) {
            log.debug("Current authenticated user: {}", principal);
            return (String) principal;
        }

        log.warn("Unknown principal type: {}", principal.getClass().getName());
        return null;
    }

    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal());
    }

    public boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String roleToCheck = role.startsWith("ROLE_") ? role : "ROLE_" + role;

        boolean hasRole = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(roleToCheck));

        log.debug("User {} has role {}: {}", getCurrentUsername(), roleToCheck, hasRole);
        return hasRole;
    }

    public boolean hasAnyRole(String... roles) {
        for (String role : roles) {
            if (hasRole(role)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAllRoles(String... roles) {
        for (String role : roles) {
            if (!hasRole(role)) {
                return false;
            }
        }
        return true;
    }

    public boolean isOwner(String resourceOwnerUsername) {
        String currentUsername = getCurrentUsername();

        if (currentUsername == null || resourceOwnerUsername == null) {
            return false;
        }

        boolean isOwner = currentUsername.equals(resourceOwnerUsername);
        log.debug("User {} is owner of resource owned by {}: {}",
                currentUsername, resourceOwnerUsername, isOwner);

        return isOwner;
    }

    public boolean isOwnerOrAdmin(String resourceOwnerUsername) {
        return isOwner(resourceOwnerUsername) || hasRole("ADMIN");
    }

    public Authentication getCurrentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public UserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            return (UserDetails) authentication.getPrincipal();
        }

        return null;
    }
}