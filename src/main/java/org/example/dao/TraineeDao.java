package org.example.dao;

import org.example.model.Trainee;
import java.util.List;

public interface TraineeDao {
    void save(Trainee trainee);
    void update(Trainee trainee);
    void delete(Trainee trainee);
    Trainee findByUsername(String username);
    List<Trainee> findAll();
}
