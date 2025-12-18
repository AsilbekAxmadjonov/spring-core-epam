// AuthenticationUtils.java
package org.example.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationUtils {

    /**
     * Extract authenticated username from request attributes
     * (set by AuthenticationInterceptor)
     */
    public static String getAuthenticatedUsername(HttpServletRequest request) {
        Object username = request.getAttribute("username");
        if (username != null) {
            return username.toString();
        }
        throw new IllegalStateException("No authenticated user found in request");
    }

    /**
     * Check if the authenticated user matches the requested username
     */
    public static boolean isAuthorizedUser(HttpServletRequest request, String requestedUsername) {
        String authenticatedUsername = getAuthenticatedUsername(request);
        return authenticatedUsername.equals(requestedUsername);
    }
}
