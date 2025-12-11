package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.config.AppConfig;
import org.example.facade.GymFacade;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;

@Slf4j
public class Main {

    public static void main(String[] args) {
        log.info("Application starting...");

        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);
        try {

            GymFacade facade = context.getBean(GymFacade.class);

            facade.showAllTrainingTypes();
            facade.createTrainerProfile("John", "Doe", "Strength");
            facade.createTraineeProfile("Ali", "Aliyev", LocalDate.of(2005, 6, 18), "Tashkent");
            facade.showAllTrainers();
            facade.showAllTrainees();
            facade.showAllTraining();

            log.info("Spring context loaded successfully.");
        } catch (Exception e) {
            log.error("Application failed to start", e);
        }

        System.out.println("Spring context started!");
    }
}
