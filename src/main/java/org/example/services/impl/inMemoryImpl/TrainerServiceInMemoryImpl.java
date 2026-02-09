package org.example.services.impl.inMemoryImpl;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.integration.workload.dto.TrainerRegistrationResponse;
import org.example.dao.TrainerDao;
import org.example.persistance.model.Trainer;
import org.example.security.AuthenticationContext;
import org.example.services.TrainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Validated
public class TrainerServiceInMemoryImpl implements TrainerService {

    private TrainerDao trainerDao;

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Override
    public TrainerRegistrationResponse createTrainer(@Valid Trainer trainer) {
        log.info("Creating new Trainer (in-memory)");

        if (trainer.getUsername() == null || trainer.getUsername().isBlank()) {
            trainer.setUsername((trainer.getFirstName() + "." + trainer.getLastName()).toLowerCase());
        }

        String tempPassword = "pass123";

        trainerDao.save(trainer);

        return TrainerRegistrationResponse.builder()
                .username(trainer.getUsername())
                .temporaryPassword(tempPassword)
                .token(null)
                .build();
    }


    @Override
    public Optional<Trainer> getTrainerByUsername(String username) {
        String authenticatedUser = AuthenticationContext.getAuthenticatedUser();

        if (authenticatedUser == null || !authenticatedUser.equals(username)) {
            throw new SecurityException("User not authenticated");
        }

        log.debug("Getting Trainer by username: {}", username);
        Trainer trainer = trainerDao.findByUsername(username);
        return Optional.ofNullable(trainer);
    }

    @Override
    public Trainer updateTrainer(String username, @Valid Trainer trainer) {
        String authenticatedUser = AuthenticationContext.getAuthenticatedUser();

        if (authenticatedUser == null || !authenticatedUser.equals(username)) {
            throw new SecurityException("User not authenticated");
        }

        log.info("Updating Trainer: {}", username);
        trainerDao.update(trainer);
        return trainer;
    }

    @Override
    public List<Trainer> getAllTrainers() {
        return trainerDao.findAll();
    }
}