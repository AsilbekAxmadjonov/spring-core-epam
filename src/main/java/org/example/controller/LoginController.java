package org.example.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.request.LoginRequest;
import org.example.security.service.AuthService;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LoginController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        String txId = UUID.randomUUID().toString();
        MDC.put("txId", txId);
        MDC.put("endpoint", "POST /api/auth/login");

        char[] password = request.getPassword();
        try {
            log.info("Login request received for username: {}", request.getUsername());

            String token = authService.login(request.getUsername(), new String(password));

            log.info("Login successful for username: {}", request.getUsername());

            return ResponseEntity.ok(Map.of(
                    "username", request.getUsername(),
                    "token", token,
                    "type", "Bearer"
            ));
        } finally {
            if (password != null) {
                Arrays.fill(password, ' ');
            }
        }
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        String txId = UUID.randomUUID().toString();
        MDC.put("txId", txId);
        MDC.put("endpoint", "POST /api/auth/logout");

        log.info("Logout request received");

        authService.logout();

        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }
}