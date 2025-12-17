package org.example.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.request.LoginRequest;
import org.example.security.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LoginController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        char[] password = request.getPassword();
        try {
            String token = authService.login(request.getUsername(), new String(password));

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "type", "Bearer"
            ));
        } catch (Exception e) {
            log.error("Login failed for user: {}", request.getUsername(), e);
            return ResponseEntity.status(401).body(Map.of(
                    "error", "Authentication failed",
                    "message", "Invalid username or password"
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
        authService.logout();
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }
}