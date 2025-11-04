package org.example.dao;

import org.example.model.Trainer;
import java.util.List;

public interface TrainerDao {
    void save(Trainer trainer);
    void update(Trainer trainer);
    Trainer findByUsername(String username);
    List<Trainer> findAll();
}
