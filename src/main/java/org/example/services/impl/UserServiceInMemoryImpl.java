package org.example.services.impl;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.dao.GenericDao;
import org.example.exception.UnsupportedDataAccessObjectException;
import org.example.model.User;
import org.example.services.AuthenticationService;
import org.example.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
public class UserServiceInMemoryImpl implements UserService {

    private final Map<Class<? extends User>, GenericDao<? extends User>> userDaos;
    private AuthenticationService authenticationService;

    public UserServiceInMemoryImpl(List<GenericDao<? extends User>> userDaos) {
        this.userDaos = userDaos.stream()
                .collect(Collectors.toMap(GenericDao::getEntityClass, Function.identity()));
    }

    @Autowired
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public User getByUsername(String username, char[] password) {
        authenticationService.authenticate(username, password);

        log.debug("Attempting to get user by username from in-memory store: {}", username);
        throw new UnsupportedOperationException("Not supported in in-memory service");
    }

    @Override
    public User getByUsername(String username) {
        log.debug("Fetching user by username (no auth): {}", username);
        throw new UnsupportedOperationException("Not supported in in-memory service");
    }


    @Override
    public User createUser(@Valid User user) {
        log.debug("Creating in-memory user with username: {}", user.getUsername());

        save(user);

        log.info("In-memory user created: {}", user.getUsername());
        return user;
    }

    @Override
    public User updateUser(String username, char[] password, @Valid User user) {
        authenticationService.authenticate(username, password);

        log.debug("Attempting to update user in in-memory store: {}", username);
        throw new UnsupportedOperationException("Not supported in in-memory service");
    }

    @Override
    public void deleteByUsername(String username, char[] password) {
        authenticationService.authenticate(username, password);

        log.debug("Attempting to delete user from in-memory store: {}", username);
        throw new UnsupportedOperationException("Not supported in in-memory service");
    }

    @Override
    public List<User> fetchAll() {
        log.debug("Fetching all users from in-memory store");

        List<User> users = userDaos.values().stream()
                .flatMap(dao -> dao.findAll().stream())
                .map(User.class::cast)
                .toList();

        log.info("Fetched {} users from in-memory store", users.size());
        return users;
    }

    @Override
    public User changeUserActiveStatus(String username, char[] password, boolean isActive) {
        authenticationService.authenticate(username, password);

        log.debug("Attempting to change in-memory user active status: {} → {}", username, isActive);
        throw new UnsupportedOperationException("Not supported in in-memory service");
    }

    private <T extends User> void saveUser(GenericDao<T> dao, User user) {
        log.debug("Saving user into DAO: {}", user.getUsername());
        dao.save(dao.getEntityClass().cast(user));
    }

    @Override
    public void save(@Valid User user) {
        GenericDao<? extends User> dao = userDaos.get(user.getClass());

        if (dao == null) {
            log.debug("No DAO found for user class: {}", user.getClass().getSimpleName());
            throw new UnsupportedDataAccessObjectException("No DAO for " + user.getClass());
        }

        saveUser(dao, user);
        log.info("User saved in-memory: {}", user.getUsername());
    }

}