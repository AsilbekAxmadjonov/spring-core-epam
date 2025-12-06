package org.example.services;

import org.example.model.User;

import java.util.List;

public interface UserService {

    User getByUsername(String username);

    User createUser(User user);

    User updateUser(String username, User user);

    void deleteByUsername(String username);

    List<User> fetchAll();

    User changeUserActiveStatus(String username, boolean isActive);

    User authenticate(String username, char[] rawPassword);

    void save(User user);
}
