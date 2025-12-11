package org.example.facade;

import org.example.model.Trainee;
import org.example.model.Trainer;
import org.example.model.Training;
import org.example.model.TrainingType;
import org.example.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class GymFacade {

    private final TrainerService trainerService;
    private final TraineeService traineeService;
    private final TrainingService trainingService;
    private final ProfileService profileService;
    private final TrainingTypeService trainingTypeService;

    @Autowired
    public GymFacade(TraineeService traineeService,
                     TrainerService trainerService,
                     TrainingService trainingService,
                     ProfileService profileService,
                     TrainingTypeService trainingTypeService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
        this.profileService = profileService;
        this.trainingTypeService = trainingTypeService;
    }

    public void createTrainerProfile(String firstName, String lastName, String specialization) {
        Trainer trainer = new Trainer();
        trainer.setFirstName(firstName);
        trainer.setLastName(lastName);
        trainer.setSpecialization(specialization);

        profileService.createProfile(trainer);
        trainerService.createTrainer(trainer);

        System.out.println("✅ Trainer profile created successfully for " + firstName + " " + lastName);
    }

    public void createTraineeProfile(String firstName, String lastName, LocalDate dateOfBirth, String address) {
        Trainee trainee = new Trainee();
        trainee.setFirstName(firstName);
        trainee.setLastName(lastName);
        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);

        profileService.createProfile(trainee);
        traineeService.createTrainee(trainee);

        System.out.println("✅ Trainee profile created successfully for " + firstName + " " + lastName);
    }

    public void showAllTrainers() {
        System.out.println("          All Trainers:");
        List<Trainer> trainers = trainerService.getAllTrainers();
        for (Trainer trainer : trainers) {
            System.out.println("Username      : " + trainer.getUsername());
            System.out.println("First Name    : " + trainer.getFirstName());
            System.out.println("Last Name     : " + trainer.getLastName());
            System.out.println("Specialization: " + trainer.getSpecialization());
            System.out.println("------------------------------");
        }
    }

    public void showAllTrainees() {
        System.out.println("          All Trainees:");
        List<Trainee> trainees = traineeService.getAllTrainees();
        for (Trainee trainee : trainees) {
            System.out.println("   Username   : " + trainee.getUsername());
            System.out.println("   Name       : " + trainee.getFirstName() + " " + trainee.getLastName());
            System.out.println("   Birth Date : " + trainee.getDateOfBirth());
            System.out.println("   Address    : " + trainee.getAddress());
            System.out.println("------------------------------");
        }
    }

    public void showAllTraining() {
        System.out.println("          All Trainings:");
        List<Training> trainings = trainingService.listAll();
        for (Training training : trainings) {
            System.out.println("   Training Name : " + training.getTrainingName());
            System.out.println("   Trainee ID    : " + training.getTraineeUsername());
            System.out.println("   Trainer ID    : " + training.getTrainerUsername());
            System.out.println("   Type          : " + training.getTrainingType());
            System.out.println("   Date          : " + training.getTrainingDate());
            System.out.println("   Duration (min): " + training.getTrainingDurationMinutes());
            System.out.println("------------------------------");
        }
    }

    public void showAllTrainingTypes() {
        System.out.println("          All Training Types:");
        List<TrainingType> trainingTypes = trainingTypeService.getAllTrainingTypes();
        for (TrainingType type : trainingTypes) {
            System.out.println("   Training Type : " + type.getTrainingTypeName());
            System.out.println("------------------------------");
        }
    }
}
