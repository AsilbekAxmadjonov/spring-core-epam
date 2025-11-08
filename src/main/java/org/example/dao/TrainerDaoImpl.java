package org.example.dao;

import lombok.extern.slf4j.Slf4j;
import org.example.model.Trainer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class TrainerDaoImpl implements TrainerDao {

    private Map<String, Trainer> trainerStorage;

    // Setter-based injection for trainerStorage
    @Autowired
    public void setTrainerStorage(@Qualifier("trainerStorage") Map<String, Trainer> trainerStorage) {
        this.trainerStorage = trainerStorage;
    }

    @Override
    public void save(Trainer trainer) {
        trainerStorage.put(trainer.getUsername(), trainer);
        log.info("Saved Trainer: {}", trainer.getUsername());
    }

    @Override
    public void update(Trainer trainer) {
        trainerStorage.put(trainer.getUsername(), trainer);
        log.info("Updated Trainer: {}", trainer.getUsername());
    }

    @Override
    public Trainer findByUsername(String username) {
        log.debug("Finding Trainer by username: {}", username);
        return trainerStorage.get(username);
    }

    @Override
    public List<Trainer> findAll() {
        log.info("Fetching all Trainers. Total count: {}", trainerStorage.size());
        return trainerStorage.values().stream().toList();
    }
}
