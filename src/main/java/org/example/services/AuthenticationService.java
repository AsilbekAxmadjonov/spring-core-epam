package org.example.services;

import org.example.model.User;

public interface AuthenticationService {
    User authenticate(String username, char[] rawPassword);
}
