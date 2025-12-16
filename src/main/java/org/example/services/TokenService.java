package org.example.services;

public interface TokenService {
    String generateToken(String username);
    String getUsernameFromToken(String token);
    boolean validateToken(String token);
    boolean isTokenExpired(String token);
}
