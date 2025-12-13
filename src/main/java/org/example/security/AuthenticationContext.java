package org.example.security;

public class AuthenticationContext {

    private static final ThreadLocal<String> authenticatedUser = new ThreadLocal<>();

    public static void setAuthenticatedUser(String username) {
        authenticatedUser.set(username);
    }

    public static String getAuthenticatedUser() {
        return authenticatedUser.get();
    }

    public static void clear() {
        authenticatedUser.remove();
    }
}

