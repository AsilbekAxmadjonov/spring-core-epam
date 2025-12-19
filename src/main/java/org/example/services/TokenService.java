package org.example.services;

import org.springframework.security.core.userdetails.UserDetails;

public interface TokenService {

    String generateToken(String username);

    String generateToken(UserDetails userDetails);

    String getUsernameFromToken(String token);

    boolean validateToken(String token);

    boolean isTokenExpired(String token);
}