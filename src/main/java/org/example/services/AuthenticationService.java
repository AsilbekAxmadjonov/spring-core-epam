package org.example.services;

import org.example.persistance.model.User;

public interface AuthenticationService {
    User authenticate(String username, char[] rawPassword);
}
