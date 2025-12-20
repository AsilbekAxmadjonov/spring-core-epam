package org.example.services;

import jakarta.validation.Valid;
import org.example.persistance.model.User;

public interface ProfileService {
    void createProfile(@Valid User user);
    boolean passwordMatches(String username, char[] rawPassword);
    void changePassword(String username, char[] newPassword);
    boolean toggleUserActiveStatus(String username);
}
