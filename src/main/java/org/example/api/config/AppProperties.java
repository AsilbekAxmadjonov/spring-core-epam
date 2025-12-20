package org.example.api.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import static org.example.security.constants.SecurityConstants.LOCALHOST_3000;
import static org.example.security.constants.SecurityConstants.LOCALHOST_4200;

@Data
@Validated
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    @NotBlank(message = "Application name cannot be blank")
    private String name;

    @Valid
    @NotNull
    private Security security = new Security();

    @Valid
    @NotNull
    private DataPaths data = new DataPaths();

    @Valid
    @NotNull
    private Cors cors = new Cors();

    @Data
    public static class Security {
        @NotBlank(message = "JWT secret cannot be blank")
        @Size(min = 32, message = "JWT secret must be at least 32 characters")
        private String jwtSecret;

        @Min(value = 1000, message = "JWT expiration must be at least 1000ms")
        private long jwtExpiration;
    }

    @Data
    public static class DataPaths {
        @Valid
        private Trainers trainers = new Trainers();

        @Valid
        private Trainees trainees = new Trainees();

        @Valid
        private Trainings trainings = new Trainings();

        @Data
        public static class Trainers {
            @NotBlank(message = "Trainers data path cannot be blank")
            private String path;
        }

        @Data
        public static class Trainees {
            @NotBlank(message = "Trainees data path cannot be blank")
            private String path;
        }

        @Data
        public static class Trainings {
            @NotBlank(message = "Trainings data path cannot be blank")
            private String path;
        }

    }
    @Data
    public static class Cors {
        private String[] allowedOrigins = {LOCALHOST_3000, LOCALHOST_4200};
        private String[] allowedMethods = {"GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"};
        private String[] allowedHeaders = {"*"};
        private String[] exposedHeaders = {"Authorization", "Content-Type"};
        private boolean allowCredentials = true;
        private long maxAge = 3600L;
    }
}