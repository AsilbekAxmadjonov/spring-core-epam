package org.example.security.configuration;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.security.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

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
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        log.info("CORS configuration enabled for allowed origins");
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
                            .requestMatchers("/api/auth/**").permitAll()

                            // Public endpoints for trainees
                            .requestMatchers("/api/trainees/**").permitAll()

                            // Public endpoints for trainings
                            .requestMatchers("/api/trainings/**").permitAll()

                            // Public POST for trainer registration
                            .requestMatchers(HttpMethod.POST, "/api/trainers").permitAll()

                            // Public endpoints for training types
                            .requestMatchers("/api/training-types/**").permitAll()

                            // Swagger/OpenAPI documentation
                            .requestMatchers(
                                    "/v3/api-docs/**",
                                    "/swagger-ui/**",
                                    "/swagger-ui.html",
                                    "/swagger-resources/**",
                                    "/webjars/**"
                            ).permitAll()

                            .requestMatchers("/actuator/**").permitAll()

                            .requestMatchers("/favicon.ico").permitAll()

                            // Error endpoint
                            .requestMatchers("/error").permitAll()

                            // All other requests require authentication
                            .anyRequest().authenticated();

                    log.info("✅ Authorization configured:");
                    log.info("  - Public: ALL /api/trainees/** (no authentication)");
                    log.info("  - Public: ALL /api/trainings/** (no authentication)");
                    log.info("  - Public: POST /api/trainers (registration only)");
                    log.info("  - Public: /api/auth/**, /api/training-types/**");
                    log.info("  - Public: Swagger UI and API docs");
                    log.info("  - Public: Actuator health endpoints");
                    log.info("  - Protected: GET/PUT/DELETE /api/trainers/** (requires JWT)");
                })
                .sessionManagement(session -> {
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                    log.info("✅ Session management set to STATELESS - JWT authentication only");
                })
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            log.warn("❌ Unauthorized access attempt to: {} - {}",
                                    request.getRequestURI(),
                                    authException.getMessage());

                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");
                            response.getWriter().write(String.format(
                                    "{\"timestamp\":\"%s\",\"status\":401,\"error\":\"Unauthorized\"," +
                                            "\"message\":\"Authentication required. Please provide a valid JWT token.\"," +
                                            "\"path\":\"%s\"}",
                                    LocalDateTime.now(),
                                    request.getRequestURI()
                            ));
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            log.warn("❌ Access denied to: {} - {}",
                                    request.getRequestURI(),
                                    accessDeniedException.getMessage());

                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");
                            response.getWriter().write(String.format(
                                    "{\"timestamp\":\"%s\",\"status\":403,\"error\":\"Forbidden\"," +
                                            "\"message\":\"Access denied. You do not have permission to access this resource.\"," +
                                            "\"path\":\"%s\"}",
                                    LocalDateTime.now(),
                                    request.getRequestURI()
                            ));
                        })
                );

        log.info("✅ Security Filter Chain configured successfully");
        return http.build();
    }

}