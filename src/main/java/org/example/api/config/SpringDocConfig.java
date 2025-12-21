package org.example.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Gym Management System API")
                        .version("1.0.0")
                        .description("""
                                RESTful API for managing gym operations including trainers, trainees, and training sessions.
                                
                                ## Authentication
                                Most endpoints require JWT authentication. To authenticate:
                                1. Register a new trainer/trainee via POST /api/trainers or POST /api/trainees
                                2. Use the returned token in the 'Authorization' header as 'Bearer <token>'
                                3. Or login via POST /api/auth/login with credentials
                                
                                ## Public Endpoints
                                - POST /api/trainers (registration)
                                - POST /api/trainees (registration)
                                - POST /api/auth/login
                                - GET /api/training-types/**
                                - GET /api/trainings/** (all training endpoints)
                                - GET /api/trainees/** (all trainee endpoints)
                                
                                ## Protected Endpoints
                                All other endpoints require a valid JWT token.
                                """)
                        .contact(new Contact()
                                .name("Gym Management Team")
                                .email("support@gym-management.example")
                                .url("https://gym-management.example"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .in(SecurityScheme.In.HEADER)
                                        .name("Authorization")
                                        .description("Enter your JWT token in the format: Bearer <token>")));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/**")
                .build();
    }
}
