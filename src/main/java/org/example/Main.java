package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.config.AppConfig;
import org.example.facade.GymFacade;
import org.example.model.*;
import org.example.security.AuthenticationContext;
import org.example.services.TraineeService;
import org.example.services.TrainerService;
import org.example.services.UserService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;
import java.util.List;

@Slf4j
public class Main {

    public static void main(String[] args) {

        AnnotationConfigApplicationContext context = null;

        try {
            context = new AnnotationConfigApplicationContext(AppConfig.class);

            GymFacade facade = context.getBean(GymFacade.class);
            TrainerService trainerService = context.getBean(TrainerService.class);
            TraineeService traineeService = context.getBean(TraineeService.class);
            UserService userService = context.getBean(UserService.class);

            System.out.println();
            System.out.println("                    GYM MANAGEMENT SYSTEM - COMPLETE DEMONSTRATION");
            System.out.println();

            System.out.println("                            SHOW ALL TRAINING TYPES");
            facade.showAllTrainingTypes();

            System.out.println("                         GET TRAINING TYPE BY NAME");
            facade.getTrainingTypeByName("Strength");
            facade.getTrainingTypeByName("Yoga");

            System.out.println("                          CREATE TRAINER PROFILES");
            facade.createTrainerProfile("John", "Doe", "Strength");
            facade.createTrainerProfile("Jane", "Smith", "Cardio");
            facade.createTrainerProfile("Mike", "Johnson", "Yoga");

            System.out.println("                          CREATE TRAINEE PROFILES");
            facade.createTraineeProfile("Ali", "Aliyev", LocalDate.of(2005, 6, 18), "Tashkent");
            facade.createTraineeProfile("Sara", "Karimova", LocalDate.of(1998, 3, 25), "Samarkand");
            facade.createTraineeProfile("Bobur", "Usmonov", LocalDate.of(2000, 11, 10), "Bukhara");

            System.out.println("                       RETRIEVING GENERATED USERNAMES");

            List<Trainer> allTrainers = trainerService.getAllTrainers();
            List<Trainee> allTrainees = traineeService.getAllTrainees();

            String trainerUsername = allTrainers.get(0).getUsername();
            String traineeUsername = allTrainees.get(0).getUsername();
            String traineeUsername2 = allTrainees.get(1).getUsername();
            String traineeUsername3 = allTrainees.get(2).getUsername();

            System.out.println("                             SHOW ALL TRAINERS");
            facade.showAllTrainers();

            System.out.println("                             SHOW ALL TRAINEES");
            facade.showAllTrainees();

            System.out.println("                               SHOW ALL USERS");
            facade.showAllUsers();

            System.out.println("                     SET AUTHENTICATION CONTEXT (TRAINER)");
            AuthenticationContext.setAuthenticatedUser(trainerUsername);

            System.out.println("                         GET TRAINER BY USERNAME");
            facade.getTrainerByUsername(trainerUsername);

            System.out.println("                           GET USER BY USERNAME");
            facade.getByUsername(trainerUsername);

            Trainer trainer = allTrainers.get(0);
            facade.passwordMatches(trainerUsername, trainer.getPassword());

            char[] newPassword = "NewSecurePass123".toCharArray();
            facade.changePassword(trainerUsername, newPassword);
            facade.passwordMatches(trainerUsername, newPassword);

            facade.toggleUserActiveStatus(trainerUsername);
            facade.toggleUserActiveStatus(trainerUsername);

            System.out.println("                    SWITCH AUTHENTICATION TO TRAINEE");
            AuthenticationContext.setAuthenticatedUser(traineeUsername);

            facade.getTraineeByUsername(traineeUsername);

            System.out.println("                        CREATE TRAINING SESSIONS");

            Training training1 = new Training();
            training1.setTrainingName("Morning Strength Training");
            training1.setTraineeUsername(traineeUsername);
            training1.setTrainerUsername(trainerUsername);
            TrainingType strength = new TrainingType();
            strength.setTrainingTypeName("Strength");
            training1.setTrainingType(strength);
            training1.setTrainingDate(LocalDate.now());
            training1.setTrainingDurationMinutes(60);
            facade.createTraining(training1);

            Training training2 = new Training();
            training2.setTrainingName("Evening Cardio Session");
            training2.setTraineeUsername(traineeUsername);
            training2.setTrainerUsername(allTrainers.get(1).getUsername());
            TrainingType cardio = new TrainingType();
            cardio.setTrainingTypeName("Cardio");
            training2.setTrainingType(cardio);
            training2.setTrainingDate(LocalDate.now().plusDays(1));
            training2.setTrainingDurationMinutes(45);
            facade.addTraining(training2);

            System.out.println("                            SHOW ALL TRAININGS");
            facade.showAllTraining();

            System.out.println("                         GET TRAINEE TRAININGS");
            facade.getTraineeTrainings(traineeUsername, null, null, null, null);

            System.out.println("                    SWITCH AUTHENTICATION TO TRAINER");
            AuthenticationContext.setAuthenticatedUser(trainerUsername);

            facade.getTrainerTrainings(trainerUsername, null, null, null);

            System.out.println("                          UPDATE TRAINER PROFILE");
            Trainer updatedTrainer = new Trainer();
            updatedTrainer.setFirstName("John");
            updatedTrainer.setLastName("Doe-Senior");
            updatedTrainer.setSpecialization("Strength");
            updatedTrainer.setIsActive(true);
            facade.updateTrainerProfile(trainerUsername, updatedTrainer);

            System.out.println("                          UPDATE TRAINEE PROFILE");
            AuthenticationContext.setAuthenticatedUser(traineeUsername);

            Trainee updatedTrainee = new Trainee();
            updatedTrainee.setFirstName("Ali");
            updatedTrainee.setLastName("Aliyev-Updated");
            updatedTrainee.setDateOfBirth(LocalDate.of(2005, 6, 18));
            updatedTrainee.setAddress("Tashkent, Yunusabad District");
            updatedTrainee.setIsActive(true);
            facade.updateTraineeProfile(traineeUsername, updatedTrainee);

            System.out.println("                          DELETE TRAINEE PROFILE");
            AuthenticationContext.setAuthenticatedUser(traineeUsername3);
            facade.deleteTraineeProfile(traineeUsername3);

            System.out.println("                          FINAL DATA SNAPSHOT");
            facade.showAllTrainingTypes();
            facade.showAllUsers();
            facade.showAllTrainers();
            facade.showAllTrainees();
            facade.showAllTraining();

            System.out.println();
            System.out.println("                     DEMO COMPLETED SUCCESSFULLY");
            System.out.println();

        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
        } finally {
            if (context != null) {
                context.close();
            }
        }

        System.out.println("                    APPLICATION TERMINATED GRACEFULLY");
    }
}
