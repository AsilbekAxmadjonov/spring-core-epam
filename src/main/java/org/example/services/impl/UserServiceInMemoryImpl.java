package org.example.services.impl;

import org.example.dao.GenericDao;
import org.example.exception.UnsupportedDataAccessObjectException;
import org.example.model.User;
import org.example.services.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserServiceInMemoryImpl implements UserService {

    private final Map<Class<? extends User>, GenericDao<? extends User>> userDaos;

    public UserServiceInMemoryImpl(List<GenericDao<? extends User>> userDaos) {
        this.userDaos = userDaos.stream()
                .collect(Collectors.toMap(GenericDao::getEntityClass, Function.identity()));
    }

    @Override
    public User getByUsername(String username) {
        throw new UnsupportedOperationException("Not supported in in-memory service");
    }

    @Override
    public User createUser(User user) {
        save(user);
        return user;
    }

    @Override
    public User updateUser(String username, User user) {
        throw new UnsupportedOperationException("Not supported in in-memory service");
    }

    @Override
    public void deleteByUsername(String username) {
        throw new UnsupportedOperationException("Not supported in in-memory service");
    }

    @Override
    public List<User> fetchAll() {
        return userDaos.values().stream()
                .flatMap(dao -> dao.findAll().stream())
                .map(User.class::cast)
                .toList();
    }

    @Override
    public User changeUserActiveStatus(String username, boolean isActive) {
        throw new UnsupportedOperationException("Not supported in in-memory service");
    }

    @Override
    public User authenticate(String username, char[] rawPassword) {
        throw new UnsupportedOperationException("Not supported in in-memory service");
    }

    private <T extends User> void saveUser(GenericDao<T> dao, User user) {
        dao.save(dao.getEntityClass().cast(user));
    }

    @Override
    public void save(User user) {
        GenericDao<? extends User> dao = userDaos.get(user.getClass());
        if (dao == null)
            throw new UnsupportedDataAccessObjectException("No DAO for " + user.getClass());
        saveUser(dao, user);
    }
}
