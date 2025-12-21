package org.example.security.service;

import org.example.services.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private GymUserDetailsService userDetailsService;

    @Mock
    private TokenService tokenService;

    @Mock
    private BruteForceProtectionService bruteForceProtectionService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_successful() {
        String username = "user1";
        String password = "pass123";
        String token = "jwt-token";

        Authentication auth = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);

        doNothing().when(bruteForceProtectionService).checkIfBlocked(username);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        doNothing().when(bruteForceProtectionService).resetAttempts(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(tokenService.generateToken(userDetails)).thenReturn(token);

        String result = authService.login(username, password);

        assertEquals(token, result);
        verify(bruteForceProtectionService).checkIfBlocked(username);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(bruteForceProtectionService).resetAttempts(username);
        verify(userDetailsService).loadUserByUsername(username);
        verify(tokenService).generateToken(userDetails);
    }

    @Test
    void login_failedWithRemainingAttempts() {
        String username = "user2";
        String password = "wrongPass";

        doNothing().when(bruteForceProtectionService).checkIfBlocked(username);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));
        when(bruteForceProtectionService.getRemainingAttempts(username)).thenReturn(2);

        BadCredentialsException exception = assertThrows(BadCredentialsException.class,
                () -> authService.login(username, password));

        assertTrue(exception.getMessage().contains("2 attempt(s) remaining"));
        verify(bruteForceProtectionService).recordFailedAttempt(username);
    }

    @Test
    void login_failedWithNoRemainingAttempts() {
        String username = "user3";
        String password = "wrongPass";

        doNothing().when(bruteForceProtectionService).checkIfBlocked(username);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));
        when(bruteForceProtectionService.getRemainingAttempts(username)).thenReturn(0);

        BadCredentialsException exception = assertThrows(BadCredentialsException.class,
                () -> authService.login(username, password));

        assertEquals("Invalid username or password", exception.getMessage());
        verify(bruteForceProtectionService).recordFailedAttempt(username);
    }


    @Test
    void logout_clearsSecurityContext() {
        authService.logout();
    }
}

