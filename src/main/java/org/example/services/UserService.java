package org.example.services;

import org.example.model.User;

import java.util.List;

public interface UserService {

    User getByUsername(String username, char[] password);

    User getByUsername(String username);

    User createUser(User user);

    User updateUser(String username, char[] password, User updatedUser);

    void deleteByUsername(String username, char[] password);

    List<User> fetchAll();

    User changeUserActiveStatus(String username, char[] password, boolean isActive);

    void save(User user);
}
