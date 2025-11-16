package org.example.dao;

import org.example.model.Trainee;

public interface TraineeDao extends GenericDao<Trainee> {
    void update(Trainee trainee);
    void delete(Trainee trainee);
    Trainee findByUsername(String username);
}
