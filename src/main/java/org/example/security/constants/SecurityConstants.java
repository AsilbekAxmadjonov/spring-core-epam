package org.example.security.constants;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

public final class SecurityConstants {

    private SecurityConstants() {
        throw new IllegalStateException("Utility class");
    }

    // JWT Constants
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String AUTHORIZATION_HEADER = HttpHeaders.AUTHORIZATION;

    // Endpoints
    public static final String API_BASE_PATH = "/api";
    public static final String AUTH_PATH = API_BASE_PATH + "/auth/**";
    public static final String TRAINERS_PATH = API_BASE_PATH + "/trainers";
    public static final String TRAINERS_ALL_PATH = API_BASE_PATH + "/trainers/**";
    public static final String TRAINEES_PATH = API_BASE_PATH + "/trainees/**";
    public static final String TRAININGS_PATH = API_BASE_PATH + "/trainings/**";
    public static final String TRAINING_TYPES_PATH = API_BASE_PATH + "/training-types/**";

    // Swagger/OpenAPI
    public static final String SWAGGER_UI_PATH = "/swagger-ui/**";
    public static final String SWAGGER_API_DOCS_PATH = "/v3/api-docs/**";
    public static final String SWAGGER_CONFIG_PATH = "/swagger-ui.html";
    public static final String SWAGGER_RESOURCES_PATH = "/swagger-resources/**";
    public static final String WEBJARS_PATH =  "/webjars/**";

    // Actuator
    public static final String ACTUATOR_ALL_PATH = "/actuator/**";

    //Favicon
    public static final String FAVICON_PATH = "/favicon.ico";

    public static final String ERROR_PATH = "/error";

    public static final String APPLICATION_JSON = "application/json";


    // HTTP Methods
    public static final String GET = HttpMethod.GET.name();
    public static final String POST = HttpMethod.POST.name();
    public static final String PUT = HttpMethod.PUT.name();
    public static final String DELETE = HttpMethod.DELETE.name();
    public static final String PATCH = HttpMethod.PATCH.name();
    public static final String OPTIONS = HttpMethod.OPTIONS.name();

    // HTTP Status Codes
    public static final int STATUS_UNAUTHORIZED = 401;
    public static final int STATUS_FORBIDDEN = 403;

    // Error Types
    public static final String ERROR_UNAUTHORIZED = "Unauthorized";
    public static final String ERROR_FORBIDDEN = "Forbidden";

    // Error Messages
    public static final String MESSAGE_UNAUTHORIZED = "Authentication required. Please provide a valid JWT token.";
    public static final String MESSAGE_FORBIDDEN = "Access denied. You do not have permission to access this resource.";

    public static final String CHARSET_UTF_8 = "UTF-8";

    public static final String LOCALHOST_3000 = "http://localhost:3000";
    public static final String LOCALHOST_4200 = "http://localhost:4200";
    public static final String[] DEFAULT_ALLOWED_ORIGINS = {LOCALHOST_3000, LOCALHOST_4200};

    // JSON Response Template
    public static final String ERROR_RESPONSE_TEMPLATE =
            "{\"timestamp\":\"%s\",\"status\":%d,\"error\":\"%s\",\"message\":\"%s\",\"path\":\"%s\"}";
}