package org.example.security.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.api.config.AppProperties;
import org.example.security.handler.JwtAccessDeniedHandler;
import org.example.security.handler.JwtAuthenticationEntryPoint;
import org.example.security.service.GymUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import static org.example.security.constants.SecurityConstants.*;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final GymUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final AppProperties appProperties;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        log.info("AuthenticationProvider configured with UserDetailsService and PasswordEncoder");
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        log.info("AuthenticationManager bean created");
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        log.info("CORS configuration enabled for allowed origins");

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(appProperties.getCors().getAllowedOrigins()));
        configuration.setAllowedMethods(Arrays.asList(appProperties.getCors().getAllowedMethods()));
        configuration.setAllowedHeaders(Arrays.asList(appProperties.getCors().getAllowedHeaders()));
        configuration.setExposedHeaders(Arrays.asList(appProperties.getCors().getExposedHeaders()));
        configuration.setAllowCredentials(appProperties.getCors().isAllowCredentials());
        configuration.setMaxAge(appProperties.getCors().getMaxAge());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("Configuring Security Filter Chain with JWT authentication");

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> {
                    log.info("Configuring authorization rules...");
                    auth
                            // Public authentication endpoints
                            .requestMatchers(AUTH_PATH).permitAll()

                            // Public endpoints for trainees
                            .requestMatchers(TRAINEES_PATH ).permitAll()

                            // Public endpoints for trainings
                            .requestMatchers(TRAININGS_PATH ).permitAll()

                            // Public POST for trainer registration
                            .requestMatchers(POST, TRAINERS_PATH).permitAll()

                            .requestMatchers(
                                    SWAGGER_API_DOCS_PATH,
                                    SWAGGER_UI_PATH,
                                    SWAGGER_CONFIG_PATH,
                                    SWAGGER_RESOURCES_PATH,
                                    WEBJARS_PATH
                            ).permitAll()

                            .requestMatchers(ACTUATOR_ALL_PATH).permitAll()

                            .requestMatchers(FAVICON_PATH).permitAll()

                            // Error endpoint
                            .requestMatchers(ERROR_PATH).permitAll()

                            // All other requests require authentication
                            .anyRequest().authenticated();

                    logAuthorizationRules();
                })
                .sessionManagement(session -> {
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                    log.info("✅ Session management set to STATELESS - JWT authentication only");
                })
                .authenticationProvider(authenticationProvider())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                );

        log.info("✅ Security Filter Chain configured successfully");
        return http.build();
    }

    private void logAuthorizationRules() {
        log.info("✅ Authorization configured:");
        log.info("  - Public: ALL {} (no authentication)", TRAINEES_PATH);
        log.info("  - Public: ALL {} (no authentication)", TRAININGS_PATH);
        log.info("  - Public: {} {} (registration only)", POST, TRAINERS_PATH);
        log.info("  - Public: {}", AUTH_PATH);
        log.info("  - Public: Swagger UI and API docs");
        log.info("  - Public: Actuator endpoints");
        log.info("  - Protected: GET/PUT/DELETE {} (requires JWT)", TRAINERS_ALL_PATH);
    }

}