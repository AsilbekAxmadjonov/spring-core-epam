package org.example.services.impl.inMemoryImpl;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.dao.GenericDao;
import org.example.exception.UnsupportedDataAccessObjectException;
import org.example.persistance.model.User;
import org.example.services.UserService;
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

    public UserServiceInMemoryImpl(List<GenericDao<? extends User>> userDaos) {
        this.userDaos = userDaos.stream()
                .collect(Collectors.toMap(GenericDao::getEntityClass, Function.identity()));
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
    public User updateUser(String username, @Valid User user) {

        log.debug("Attempting to update user in in-memory store: {}", username);
        throw new UnsupportedOperationException("Not supported in in-memory service");
    }

    @Override
    public void deleteByUsername(String username) {
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