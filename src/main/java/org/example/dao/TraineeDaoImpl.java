package org.example.dao;

import lombok.extern.slf4j.Slf4j;
import org.example.model.Trainee;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class TraineeDaoImpl implements TraineeDao {

    private Map<String, Trainee> traineeStorage;

    // Setter-based injection for the storage map
    @Autowired
    public void setTraineeStorage(@Qualifier("traineeStorage") Map<String, Trainee> traineeStorage) {
        this.traineeStorage = traineeStorage;
    }

    @Override
    public void save(Trainee trainee) {
        traineeStorage.put(trainee.getUsername(), trainee);
        log.info("Saved new Trainee: {}", trainee.getUsername());
    }

    @Override
    public void update(Trainee trainee) {
        traineeStorage.put(trainee.getUsername(), trainee);
        log.info("Updated Trainee: {}", trainee.getUsername());
    }

    @Override
    public void delete(Trainee trainee) {
        if (trainee == null || trainee.getUsername() == null) {
            log.warn("Attempted to delete null Trainee or Trainee without username");
            return;
        }

        if (traineeStorage.containsKey(trainee.getUsername())) {
            traineeStorage.remove(trainee.getUsername());
            log.info("Deleted Trainee: {}", trainee.getUsername());
        } else {
            log.warn("Attempted to delete non-existent Trainee: {}", trainee.getUsername());
        }
    }

    @Override
    public Trainee findByUsername(String username) {
        log.debug("Finding Trainee by username: {}", username);
        return traineeStorage.get(username);
    }

    @Override
    public List<Trainee> findAll() {
        log.info("Fetching all Trainees. Total count: {}", traineeStorage.size());
        return traineeStorage.values().stream().toList();
    }
}
