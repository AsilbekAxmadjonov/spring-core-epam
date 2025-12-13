package org.example.services;

import org.example.model.User;

import java.util.List;

public interface UserService {

    User getByUsername(String username);

    User createUser(User user);

    User updateUser(String username, User updatedUser);

    void deleteByUsername(String username);

    List<User> fetchAll();

    void save(User user);
}
