package org.example.security.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.security.GymUserDetails;
import org.example.security.service.GymUserDetailsService;
import org.example.services.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private GymUserDetailsService gymUserDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private GymUserDetails userDetails;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private static final String VALID_TOKEN = "valid.jwt.token";
    private static final String INVALID_TOKEN = "invalid.jwt.token";
    private static final String USERNAME = "testuser";
    private static final String BEARER_TOKEN = "Bearer " + VALID_TOKEN;
    private static final String PROTECTED_URI = "/api/users/profile";

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
        when(request.getMethod()).thenReturn("GET");
    }

    @Test
    void doFilterInternal_WithPublicEndpoint_ShouldSkipAuthentication() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/auth/login");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verify(tokenService, never()).getUsernameFromToken(any());
        verify(gymUserDetailsService, never()).loadUserByUsername(any());
    }

    @Test
    void doFilterInternal_WithSwaggerEndpoint_ShouldSkipAuthentication() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/swagger-ui/index.html");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verify(tokenService, never()).getUsernameFromToken(any());
    }

    @Test
    void doFilterInternal_WithNoAuthorizationHeader_ShouldContinueFilterChain() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn(PROTECTED_URI);
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verify(tokenService, never()).getUsernameFromToken(any());
    }

    @Test
    void doFilterInternal_WithInvalidAuthorizationHeaderFormat_ShouldContinueFilterChain() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn(PROTECTED_URI);
        when(request.getHeader("Authorization")).thenReturn("InvalidFormat token");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verify(tokenService, never()).getUsernameFromToken(any());
    }

    @Test
    void doFilterInternal_WithValidToken_ShouldAuthenticateUser() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn(PROTECTED_URI);
        when(request.getHeader("Authorization")).thenReturn(BEARER_TOKEN);
        when(tokenService.getUsernameFromToken(VALID_TOKEN)).thenReturn(USERNAME);
        when(securityContext.getAuthentication()).thenReturn(null);
        when(gymUserDetailsService.loadUserByUsername(USERNAME)).thenReturn(userDetails);
        when(tokenService.validateToken(VALID_TOKEN)).thenReturn(true);
        when(userDetails.isEnabled()).thenReturn(true);
        when(userDetails.isAccountNonLocked()).thenReturn(true);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(tokenService).getUsernameFromToken(VALID_TOKEN);
        verify(gymUserDetailsService).loadUserByUsername(USERNAME);
        verify(tokenService).validateToken(VALID_TOKEN);
        verify(securityContext).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithInvalidToken_ShouldNotAuthenticate() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn(PROTECTED_URI);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + INVALID_TOKEN);
        when(tokenService.getUsernameFromToken(INVALID_TOKEN)).thenReturn(USERNAME);
        when(securityContext.getAuthentication()).thenReturn(null);
        when(gymUserDetailsService.loadUserByUsername(USERNAME)).thenReturn(userDetails);
        when(tokenService.validateToken(INVALID_TOKEN)).thenReturn(false);
        when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());
        when(userDetails.isEnabled()).thenReturn(true);
        when(userDetails.isAccountNonLocked()).thenReturn(true);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(tokenService).validateToken(INVALID_TOKEN);
        verify(securityContext, never()).setAuthentication(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithExpiredToken_ShouldClearSecurityContext() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn(PROTECTED_URI);
        when(request.getHeader("Authorization")).thenReturn(BEARER_TOKEN);
        when(tokenService.getUsernameFromToken(VALID_TOKEN)).thenThrow(new ExpiredJwtException(null, null, "Token expired"));

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithMalformedToken_ShouldClearSecurityContext() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn(PROTECTED_URI);
        when(request.getHeader("Authorization")).thenReturn(BEARER_TOKEN);
        when(tokenService.getUsernameFromToken(VALID_TOKEN)).thenThrow(new MalformedJwtException("Malformed token"));

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithInvalidSignature_ShouldClearSecurityContext() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn(PROTECTED_URI);
        when(request.getHeader("Authorization")).thenReturn(BEARER_TOKEN);
        when(tokenService.getUsernameFromToken(VALID_TOKEN)).thenThrow(new SignatureException("Invalid signature"));

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithUserNotFound_ShouldClearSecurityContext() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn(PROTECTED_URI);
        when(request.getHeader("Authorization")).thenReturn(BEARER_TOKEN);
        when(tokenService.getUsernameFromToken(VALID_TOKEN)).thenReturn(USERNAME);
        when(securityContext.getAuthentication()).thenReturn(null);
        when(gymUserDetailsService.loadUserByUsername(USERNAME)).thenThrow(new UsernameNotFoundException("User not found"));

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithNullUsername_ShouldNotAuthenticate() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn(PROTECTED_URI);
        when(request.getHeader("Authorization")).thenReturn(BEARER_TOKEN);
        when(tokenService.getUsernameFromToken(VALID_TOKEN)).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(gymUserDetailsService, never()).loadUserByUsername(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithExistingAuthentication_ShouldSkipReAuthentication() throws ServletException, IOException {
        // Arrange
        Authentication existingAuth = mock(Authentication.class);
        when(request.getRequestURI()).thenReturn(PROTECTED_URI);
        when(request.getHeader("Authorization")).thenReturn(BEARER_TOKEN);
        when(tokenService.getUsernameFromToken(VALID_TOKEN)).thenReturn(USERNAME);
        when(securityContext.getAuthentication()).thenReturn(existingAuth);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(gymUserDetailsService, never()).loadUserByUsername(any());
        verify(tokenService, never()).validateToken(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithGenericException_ShouldClearSecurityContext() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn(PROTECTED_URI);
        when(request.getHeader("Authorization")).thenReturn(BEARER_TOKEN);
        when(tokenService.getUsernameFromToken(VALID_TOKEN)).thenThrow(new RuntimeException("Unexpected error"));

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithTraineesEndpoint_ShouldSkipAuthentication() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/trainees/register");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verify(tokenService, never()).getUsernameFromToken(any());
    }

    @Test
    void doFilterInternal_WithActuatorHealthEndpoint_ShouldSkipAuthentication() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/actuator/health");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verify(tokenService, never()).getUsernameFromToken(any());
    }

    @Test
    void doFilterInternal_WithErrorEndpoint_ShouldSkipAuthentication() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/error");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verify(tokenService, never()).getUsernameFromToken(any());
    }

    @Test
    void doFilterInternal_WithEmptyBearerToken_ShouldNotAuthenticate() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn(PROTECTED_URI);
        when(request.getHeader("Authorization")).thenReturn("Bearer ");
        when(tokenService.getUsernameFromToken("")).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(tokenService).getUsernameFromToken("");
        verify(gymUserDetailsService, never()).loadUserByUsername(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_AlwaysCallsFilterChain() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn(PROTECTED_URI);
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
    }
}