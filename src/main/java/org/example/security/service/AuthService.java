package org.example.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.services.TokenService;
import org.slf4j.MDC;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final GymUserDetailsService userDetailsService;
    private final TokenService tokenService;
    private final BruteForceProtectionService bruteForceProtectionService;

    public String login(String username, String password) {
        MDC.put("operation", "login");
        MDC.put("username", username);

        log.info("Login attempt for user: {}", username);

        // Check if user is blocked (throws UserBlockedException if blocked)
        bruteForceProtectionService.checkIfBlocked(username);

        try {
            // Authenticate user
            UsernamePasswordAuthenticationToken authRequest =
                    new UsernamePasswordAuthenticationToken(username, password);

            Authentication authentication = authenticationManager.authenticate(authRequest);

            // Reset failed login attempts on successful authentication
            bruteForceProtectionService.resetAttempts(username);

            // Load user details
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Generate token
            String token = tokenService.generateToken(userDetails);

            log.info("User {} logged in successfully with JWT", username);
            return token;

        } catch (AuthenticationException e) {
            log.error("Authentication failed for user: {} - {}", username, e.getMessage());

            // Record failed login attempt
            bruteForceProtectionService.recordFailedAttempt(username);

            // Get remaining attempts
            int remainingAttempts = bruteForceProtectionService.getRemainingAttempts(username);

            if (remainingAttempts > 0) {
                throw new BadCredentialsException(
                        String.format("Invalid username or password. %d attempt(s) remaining.", remainingAttempts)
                );
            } else {
                throw new BadCredentialsException("Invalid username or password");
            }
        }
    }

    public void logout() {
        SecurityContextHolder.clearContext();
        log.debug("Logged out successfully");
    }
}