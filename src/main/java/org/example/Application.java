package org.example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;

@Slf4j
@SpringBootApplication
@EnableScheduling
public class Application {

    public static void main(String[] args) {
        log.info("Starting Gym Management REST API with Spring Boot...");

        SpringApplication app = new SpringApplication(Application.class);
        ApplicationContext ctx = app.run(args);

        Environment env = ctx.getEnvironment();
        String activeProfiles = String.join(", ", env.getActiveProfiles());

        log.info("✓ Spring Boot application started successfully");
        log.info("✓ Server running on http://localhost:8080");
        log.info("✓ API base path: http://localhost:8080/api");
        log.info("✓ API documentation: http://localhost:8080/swagger-ui.html");
        log.info("✓ Spring Security JWT authentication is ACTIVE");

        String[] profiles = env.getActiveProfiles();
        if (profiles.length == 0) {
            profiles = env.getDefaultProfiles();
        }
        log.info("✓ Active Spring profile(s): {}", Arrays.toString(profiles));


        //Actuator links:
        log.info("✓ Actuator health: http://localhost:8080/actuator/health");
        log.info("✓ Actuator metrics: http://localhost:8080/actuator/metrics");
        log.info("✓ Actuator prometheus: http://localhost:8080/actuator/prometheus");

    }
}