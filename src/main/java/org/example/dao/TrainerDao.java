package org.example.dao;

import org.example.model.Trainer;
import java.util.List;

public interface TrainerDao extends GenericDao<Trainer> {
    void update(Trainer trainer);
    Trainer findByUsername(String username);
}
