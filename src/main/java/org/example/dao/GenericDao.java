package org.example.dao;

import org.example.model.User;
import java.util.List;

public interface GenericDao<T> {
    void save(T user);
    List<T> findAll();
    Class<T> getEntityClass();
}
