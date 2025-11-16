package org.example.service.impl;

import org.example.dao.GenericDao;
import org.example.exception.UnsupportedDataAccessObjectException;
import org.example.model.User;
import org.example.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final Map<Class<? extends User>, GenericDao<? extends User>> userDaos;

    public UserServiceImpl(List<GenericDao<? extends User>> userDaos) {
        this.userDaos = userDaos.stream()
                .collect(Collectors.toMap(GenericDao::getEntityClass, Function.identity()));
    }

    @Override
    public void save(User user) {
        GenericDao<? extends User> dao = userDaos.get(user.getClass());
        // ⚠️ introduce separate exception
        if (dao == null) throw new UnsupportedDataAccessObjectException("No DAO for " + user.getClass());
        saveUser(dao, user);
    }

    private <T extends User> void saveUser(GenericDao<T> dao, User user) {
        dao.save(dao.getEntityClass().cast(user));
    }

    @Override
    public List<User> findAll() {
        return userDaos.values().stream()
                .flatMap(dao -> dao.findAll().stream())
                .map(User.class::cast)
                .toList();
    }
}
