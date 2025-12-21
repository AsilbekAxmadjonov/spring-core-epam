package org.example.services;

import jakarta.validation.Valid;
import org.example.persistance.model.User;

import java.util.List;

public interface UserService {

    User getByUsername(String username);

    User createUser(@Valid User user);

    User updateUser(String username, @Valid User updatedUser);

    void deleteByUsername(String username);

    List<User> fetchAll();

    void save(@Valid User user);
}
