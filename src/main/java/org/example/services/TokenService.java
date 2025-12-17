package org.example.services;

import org.springframework.security.core.userdetails.UserDetails;

public interface TokenService {

    /**
     * Generate JWT token from username
     */
    String generateToken(String username);

    /**
     * Generate JWT token from UserDetails (recommended for proper authentication)
     */
    String generateToken(UserDetails userDetails);

    /**
     * Extract username from JWT token
     */
    String getUsernameFromToken(String token);

    /**
     * Validate JWT token
     */
    boolean validateToken(String token);

    /**
     * Check if token is expired
     */
    boolean isTokenExpired(String token);
}