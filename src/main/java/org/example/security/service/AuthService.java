package org.example.security.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.services.TokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final TokenService tokenService;

    public String login(String username, String password) {
        UsernamePasswordAuthenticationToken authRequest =
                new UsernamePasswordAuthenticationToken(username, password);

        authenticationManager.authenticate(authRequest);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        String token = tokenService.generateToken(userDetails);

        log.debug("User {} logged in successfully with JWT", username);
        return token;
    }

    public void logout() {
        SecurityContextHolder.clearContext();
        log.debug("Logged out successfully");
    }
}
