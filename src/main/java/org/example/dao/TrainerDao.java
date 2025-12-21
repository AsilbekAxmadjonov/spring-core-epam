package org.example.dao;

import org.example.persistance.model.Trainer;

public interface TrainerDao extends GenericDao<Trainer> {
    void update(Trainer trainer);
    Trainer findByUsername(String username);
}
