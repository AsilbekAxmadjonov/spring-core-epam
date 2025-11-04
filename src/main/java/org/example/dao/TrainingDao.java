package org.example.dao;

import org.example.model.Training;
import java.util.List;

public interface TrainingDao {
    void save(Training training);
    Training findByName(String name);
    List<Training> findAll();
}
