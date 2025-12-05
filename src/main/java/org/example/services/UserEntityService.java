package org.example.services;

import org.example.model.User;

import java.util.List;

public interface UserEntityService {

    User getByUsername(String username);

    User createUser(User user);

    User updateUser(String username, User user);

    void deleteUser(String username);

    List<User> getAllUsers();

    User changeActiveStatus(String username, boolean isActive);

    User authenticate(String username, char[] rawPassword);
}
