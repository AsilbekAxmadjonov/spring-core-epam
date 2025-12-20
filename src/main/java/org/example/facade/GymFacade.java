package org.example.facade;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.api.dto.request.TrainingRequest;
import org.example.persistance.model.*;
import org.example.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class GymFacade {

    private final TrainerService trainerService;
    private final TraineeService traineeService;
    private final TrainingService trainingService;
    private final ProfileService profileService;
    private final TrainingTypeService trainingTypeService;
    private final UserService userService;

    @Autowired
    public GymFacade(TraineeService traineeService,
                     TrainerService trainerService,
                     TrainingService trainingService,
                     ProfileService profileService,
                     TrainingTypeService trainingTypeService,
                     UserService userService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
        this.profileService = profileService;
        this.trainingTypeService = trainingTypeService;
        this.userService = userService;
    }

    public boolean passwordMatches(String username, char[] rawPassword) {
        log.info("=== Checking Password Match ===");
        System.out.println("Checking password for: " + username);

        boolean matches = profileService.passwordMatches(username, rawPassword);

        System.out.println(matches ? "✓ Password matches" : "✗ Password does not match");
        System.out.println();
        return matches;
    }

    public void changePassword(String username, char[] newPassword) {
        log.info("=== Changing Password ===");
        System.out.println("Changing password for: " + username);

        profileService.changePassword(username, newPassword);

        System.out.println("✓ Password changed successfully");
        System.out.println();
    }

    public boolean toggleUserActiveStatus(String username) {
        log.info("=== Toggling User Active Status ===");
        System.out.println("Toggling active status for: " + username);

        boolean newStatus = profileService.toggleUserActiveStatus(username);

        System.out.println("✓ User active status is now: " + newStatus);
        System.out.println();
        return newStatus;
    }

    public void createTrainerProfile(String firstName, String lastName, String specialization) {
        log.info("=== Creating Trainer Profile ===");
        System.out.println("Creating trainer: " + firstName + " " + lastName);
        System.out.println("Specialization: " + specialization);

        Trainer trainer = new Trainer();
        trainer.setFirstName(firstName);
        trainer.setLastName(lastName);
        trainer.setSpecialization(specialization);

        profileService.createProfile(trainer);
        trainerService.createTrainer(trainer);

        System.out.println("✅ Trainer profile created successfully");
        System.out.println("Username: " + trainer.getUsername());
        System.out.println("Password: " + trainer.getPassword());
        System.out.println();
    }

    public Optional<Trainer> getTrainerByUsername(String username) {
        log.info("=== Getting Trainer by Username ===");
        System.out.println("Fetching trainer: " + username);

        Optional<Trainer> trainer = trainerService.getTrainerByUsername(username);

        if (trainer.isPresent()) {
            System.out.println("✓ Trainer found: " + trainer.get().getFirstName() + " " + trainer.get().getLastName());
        } else {
            System.out.println("✗ Trainer not found");
        }
        System.out.println();
        return trainer;
    }

    public void updateTrainerProfile(String username, Trainer updatedTrainer) {
        log.info("=== Updating Trainer Profile ===");
        System.out.println("Updating trainer: " + username);

        Trainer updated = trainerService.updateTrainer(username, updatedTrainer);

        System.out.println("✓ Trainer updated successfully");
        System.out.println("New name: " + updated.getFirstName() + " " + updated.getLastName());
        System.out.println();
    }

    public void showAllTrainers() {
        log.info("=== Showing All Trainers ===");
        System.out.println("          All Trainers:");
        List<Trainer> trainers = trainerService.getAllTrainers();
        for (Trainer trainer : trainers) {
            System.out.println("Username      : " + trainer.getUsername());
            System.out.println("First Name    : " + trainer.getFirstName());
            System.out.println("Last Name     : " + trainer.getLastName());
            System.out.println("Specialization: " + trainer.getSpecialization());
            System.out.println("Active        : " + trainer.getIsActive());
            System.out.println("------------------------------");
        }
        System.out.println("Total trainers: " + trainers.size());
        System.out.println();
    }

    public void createTraineeProfile(String firstName, String lastName, LocalDate dateOfBirth, String address) {
        log.info("=== Creating Trainee Profile ===");
        System.out.println("Creating trainee: " + firstName + " " + lastName);
        System.out.println("Date of Birth: " + dateOfBirth);
        System.out.println("Address: " + address);

        Trainee trainee = new Trainee();
        trainee.setFirstName(firstName);
        trainee.setLastName(lastName);
        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);

        profileService.createProfile(trainee);
        traineeService.createTrainee(trainee);

        System.out.println("✅ Trainee profile created successfully");
        System.out.println("Username: " + trainee.getUsername());
        System.out.println("Password: " + trainee.getPassword());
        System.out.println();
    }

    public Optional<Trainee> getTraineeByUsername(String username) {
        log.info("=== Getting Trainee by Username ===");
        System.out.println("Fetching trainee: " + username);

        Optional<Trainee> trainee = traineeService.getTraineeByUsername(username);

        if (trainee.isPresent()) {
            System.out.println("✓ Trainee found: " + trainee.get().getFirstName() + " " + trainee.get().getLastName());
        } else {
            System.out.println("✗ Trainee not found");
        }
        System.out.println();
        return trainee;
    }

    public void updateTraineeProfile(String username, Trainee updatedTrainee) {
        log.info("=== Updating Trainee Profile ===");
        System.out.println("Updating trainee: " + username);

        Trainee updated = traineeService.updateTrainee(username, updatedTrainee);

        System.out.println("✓ Trainee updated successfully");
        System.out.println("New name: " + updated.getFirstName() + " " + updated.getLastName());
        System.out.println();
    }

    public void deleteTraineeProfile(String username) {
        log.info("=== Deleting Trainee Profile ===");
        System.out.println("Deleting trainee: " + username);

        traineeService.deleteTraineeByUsername(username);

        System.out.println("✓ Trainee deleted successfully");
        System.out.println();
    }

    public void showAllTrainees() {
        log.info("=== Showing All Trainees ===");
        System.out.println("          All Trainees:");
        List<Trainee> trainees = traineeService.getAllTrainees();
        for (Trainee trainee : trainees) {
            System.out.println("   Username   : " + trainee.getUsername());
            System.out.println("   Name       : " + trainee.getFirstName() + " " + trainee.getLastName());
            System.out.println("   Birth Date : " + trainee.getDateOfBirth());
            System.out.println("   Address    : " + trainee.getAddress());
            System.out.println("   Active     : " + trainee.getIsActive());
            System.out.println("------------------------------");
        }
        System.out.println("Total trainees: " + trainees.size());
        System.out.println();
    }

    public List<Training> getTraineeTrainings(String traineeUsername, LocalDate fromDate,
                                              LocalDate toDate, String trainerName, String trainingType) {
        log.info("=== Getting Trainee Trainings ===");
        System.out.println("Fetching trainings for trainee: " + traineeUsername);

        List<Training> trainings = trainingService.getTraineeTrainings(
                traineeUsername, fromDate, toDate, trainerName, trainingType);

        System.out.println("✓ Found " + trainings.size() + " trainings");
        System.out.println();
        return trainings;
    }

    public List<Training> getTrainerTrainings(String trainerUsername, LocalDate fromDate,
                                              LocalDate toDate, String traineeName) {
        log.info("=== Getting Trainer Trainings ===");
        System.out.println("Fetching trainings for trainer: " + trainerUsername);

        List<Training> trainings = trainingService.getTrainerTrainings(
                trainerUsername, fromDate, toDate, traineeName);

        System.out.println("✓ Found " + trainings.size() + " trainings");
        System.out.println();
        return trainings;
    }

    public Training createTraining(TrainingRequest request) {
        return trainingService.createTraining(request);
    }


    public void showAllTraining() {
        log.info("=== Showing All Training Sessions ===");
        System.out.println("          All Trainings:");
        List<Training> trainings = trainingService.listAll();
        for (Training training : trainings) {
            System.out.println("   Training Name : " + training.getTrainingName());
            System.out.println("   Trainee       : " + training.getTraineeUsername());
            System.out.println("   Trainer       : " + training.getTrainerUsername());
            System.out.println("   Type          : " + training.getTrainingType());
            System.out.println("   Date          : " + training.getTrainingDate());
            System.out.println("   Duration (min): " + training.getTrainingDurationMinutes());
            System.out.println("------------------------------");
        }
        System.out.println("Total training sessions: " + trainings.size());
        System.out.println();
    }

    public Optional<TrainingType> getTrainingTypeByName(String name) {
        log.info("=== Getting Training Type by Name ===");
        System.out.println("Fetching training type: " + name);

        Optional<TrainingType> type = trainingTypeService.getTrainingTypeByName(name);

        if (type.isPresent()) {
            System.out.println("✓ Training type found: " + type.get().getTrainingTypeName());
        } else {
            System.out.println("✗ Training type not found");
        }
        System.out.println();
        return type;
    }

    public void showAllTrainingTypes() {
        log.info("=== Showing All Training Types ===");
        System.out.println("          All Training Types:");
        List<TrainingType> trainingTypes = trainingTypeService.getAllTrainingTypes();
        for (TrainingType type : trainingTypes) {
            System.out.println("   Training Type : " + type.getTrainingTypeName());
            System.out.println("------------------------------");
        }
        System.out.println("Total training types: " + trainingTypes.size());
        System.out.println();
    }

    public User getByUsername(String username) {
        log.info("=== Getting User by Username ===");
        System.out.println("Fetching user: " + username);

        User user = userService.getByUsername(username);

        System.out.println("✓ User found: " + user.getFirstName() + " " + user.getLastName());
        System.out.println();
        return user;
    }

    public void showAllUsers() {
        log.info("=== Showing All Users ===");
        System.out.println("          All Users:");
        List<User> users = userService.fetchAll();
        for (User user : users) {
            System.out.println("   Username  : " + user.getUsername());
            System.out.println("   Name      : " + user.getFirstName() + " " + user.getLastName());
            System.out.println("   Active    : " + user.getIsActive());
            System.out.println("------------------------------");
        }
        System.out.println("Total users: " + users.size());
        System.out.println();
    }
}