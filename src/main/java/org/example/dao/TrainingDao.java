package org.example.dao;

import org.example.model.Training;

public interface TrainingDao extends GenericDao<Training>{
    Training findByName(String name);
}
