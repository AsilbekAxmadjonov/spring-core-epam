package org.example.dao;

import org.example.model.User;

import java.util.List;

public interface ProfileSavableDao {
    void save(User user);
    List<String> findAllUsernames();
}
