package org.example.services;

import org.example.model.User;

public interface ProfileService {
    void createProfile(User user);
    boolean passwordMatches(String username, char[] rawPassword);
    void changePassword(String username,char[] oldPassword, char[] newPassword);
    void setActiveStatus(String username,char[] password, boolean active);
}
