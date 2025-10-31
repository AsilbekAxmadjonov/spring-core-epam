package org.example;

import org.example.config.AppConfig;
import org.example.facade.GymFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Application starting...");

        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(AppConfig.class)) {

            GymFacade facade = context.getBean(GymFacade.class);
            facade.createTrainerProfile("John", "Doe", "Strength");
            facade.createTraineeProfile("ali", "aliyev", LocalDate.of(2005,06,18), "Tashkent");
            facade.showAllTrainers();
            facade.showAllTrainees();
            facade.showAllTraining();

            logger.info("Spring context loaded successfully.");
        } catch (Exception e) {
            logger.error("Application failed to start", e);
        }
    }
}
