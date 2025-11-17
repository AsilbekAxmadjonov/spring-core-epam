package org.example.util;

import org.example.model.User;

public class TestUser extends User {
    public TestUser(String firstName, String lastName, String username, char[] password, boolean isActive) {
        super(firstName, lastName, username, password, isActive);
    }
}
