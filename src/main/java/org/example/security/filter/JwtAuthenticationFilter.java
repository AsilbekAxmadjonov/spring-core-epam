package org.example.security.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.services.TokenService;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter
 * Intercepts all requests and validates JWT tokens from Authorization header
 * Extends OncePerRequestFilter to ensure single execution per request
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final int BEARER_PREFIX_LENGTH = 7;

    private final TokenService tokenService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String requestURI = request.getRequestURI();
        final String requestMethod = request.getMethod();

        log.debug("=== JWT Filter Processing: {} {} ===", requestMethod, requestURI);

        // Skip filter for public endpoints
        if (isPublicEndpoint(requestURI)) {
            log.debug("Public endpoint detected: {} - Skipping JWT validation", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extract JWT token from request
            String jwt = extractJwtFromRequest(request);

            if (jwt == null) {
                log.debug("No Bearer token found for: {} - Passing to next filter", requestURI);
                filterChain.doFilter(request, response);
                return;
            }

            log.debug("JWT Token extracted (first 20 chars): {}...",
                    jwt.substring(0, Math.min(jwt.length(), 20)));

            // Extract username from token
            String username = tokenService.getUsernameFromToken(jwt);

            if (username == null) {
                log.warn("❌ Username could not be extracted from token for request: {}", requestURI);
                filterChain.doFilter(request, response);
                return;
            }

            log.debug("Username extracted from token: {}", username);

            // Authenticate if not already authenticated
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                authenticateUser(request, jwt, username, requestURI);
            } else {
                log.debug("User already authenticated in SecurityContext");
            }

        } catch (ExpiredJwtException e) {
            log.warn("❌ JWT token expired for request: {} - {}", requestURI, e.getMessage());
            SecurityContextHolder.clearContext();
        } catch (MalformedJwtException e) {
            log.warn("❌ Malformed JWT token for request: {} - {}", requestURI, e.getMessage());
            SecurityContextHolder.clearContext();
        } catch (SignatureException e) {
            log.warn("❌ Invalid JWT signature for request: {} - {}", requestURI, e.getMessage());
            SecurityContextHolder.clearContext();
        } catch (UsernameNotFoundException e) {
            log.warn("❌ User not found: {} for request: {}", e.getMessage(), requestURI);
            SecurityContextHolder.clearContext();
        } catch (Exception e) {
            log.error("❌ JWT Authentication failed for {}: {}", requestURI, e.getMessage(), e);
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from Authorization header
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(authHeader) && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX_LENGTH);
        }

        return null;
    }

    /**
     * Authenticate user and set authentication in SecurityContext
     */
    private void authenticateUser(HttpServletRequest request, String jwt, String username, String requestURI) {
        log.debug("Loading user details for: {}", username);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        log.debug("User details loaded. Enabled: {}, AccountNonLocked: {}, Authorities: {}",
                userDetails.isEnabled(),
                userDetails.isAccountNonLocked(),
                userDetails.getAuthorities());

        if (tokenService.validateToken(jwt)) {
            log.info("✅ JWT token valid for user: {} accessing: {}", username, requestURI);

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);

            log.debug("Authentication set in SecurityContext for user: {} with authorities: {}",
                    username, userDetails.getAuthorities());
        } else {
            log.warn("❌ Invalid JWT token for user: {}", username);
        }
    }

    /**
     * Check if the endpoint is public and doesn't require authentication
     */
    private boolean isPublicEndpoint(String requestURI) {
        return requestURI.startsWith("/api/auth/") ||
                requestURI.startsWith("/api/trainees/") ||
                requestURI.startsWith("/api/trainings/") ||
                requestURI.startsWith("/api/training-types/") ||
                requestURI.startsWith("/swagger-ui") ||
                requestURI.startsWith("/v3/api-docs") ||
                requestURI.startsWith("/actuator/health") ||
                requestURI.startsWith("/actuator/info") ||
                requestURI.equals("/error");
    }
}