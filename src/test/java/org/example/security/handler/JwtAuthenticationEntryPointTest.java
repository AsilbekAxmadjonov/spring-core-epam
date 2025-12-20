package org.example.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.example.security.constants.SecurityConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationEntryPointTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private StringWriter stringWriter;
    private PrintWriter printWriter;
    private AuthenticationException authenticationException;

    private static final String TEST_URI = "/api/secure/resource";
    private static final String TEST_ERROR_MESSAGE = "Invalid credentials";

    @BeforeEach
    void setUp() throws IOException {
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
        authenticationException = new BadCredentialsException(TEST_ERROR_MESSAGE);

        when(response.getWriter()).thenReturn(printWriter);
        when(request.getRequestURI()).thenReturn(TEST_URI);
    }

    @Test
    void commence_ShouldSetCorrectResponseStatus() throws ServletException, IOException {
        // Act
        jwtAuthenticationEntryPoint.commence(request, response, authenticationException);

        // Assert
        verify(response).setStatus(SC_UNAUTHORIZED);
    }

    @Test
    void commence_ShouldSetCorrectContentType() throws ServletException, IOException {
        // Act
        jwtAuthenticationEntryPoint.commence(request, response, authenticationException);

        // Assert
        verify(response).setContentType(APPLICATION_JSON);
    }

    @Test
    void commence_ShouldSetCorrectCharacterEncoding() throws ServletException, IOException {
        // Act
        jwtAuthenticationEntryPoint.commence(request, response, authenticationException);

        // Assert
        verify(response).setCharacterEncoding(CHARSET_UTF_8);
    }

    @Test
    void commence_ShouldWriteErrorResponseToWriter() throws ServletException, IOException {
        // Act
        jwtAuthenticationEntryPoint.commence(request, response, authenticationException);
        printWriter.flush();

        // Assert
        String responseBody = stringWriter.toString();
        assertNotNull(responseBody);
        assertFalse(responseBody.isEmpty());
        verify(response).getWriter();
    }

    @Test
    void commence_ShouldIncludeStatusInResponse() throws ServletException, IOException {
        // Act
        jwtAuthenticationEntryPoint.commence(request, response, authenticationException);
        printWriter.flush();

        // Assert
        String responseBody = stringWriter.toString();
        assertTrue(responseBody.contains(String.valueOf(STATUS_UNAUTHORIZED)));
    }

    @Test
    void commence_ShouldIncludeErrorTypeInResponse() throws ServletException, IOException {
        // Act
        jwtAuthenticationEntryPoint.commence(request, response, authenticationException);
        printWriter.flush();

        // Assert
        String responseBody = stringWriter.toString();
        assertTrue(responseBody.contains(ERROR_UNAUTHORIZED));
    }

    @Test
    void commence_ShouldIncludeErrorMessageInResponse() throws ServletException, IOException {
        // Act
        jwtAuthenticationEntryPoint.commence(request, response, authenticationException);
        printWriter.flush();

        // Assert
        String responseBody = stringWriter.toString();
        assertTrue(responseBody.contains(MESSAGE_UNAUTHORIZED));
    }

    @Test
    void commence_ShouldIncludeRequestPathInResponse() throws ServletException, IOException {
        // Act
        jwtAuthenticationEntryPoint.commence(request, response, authenticationException);
        printWriter.flush();

        // Assert
        String responseBody = stringWriter.toString();
        assertTrue(responseBody.contains(TEST_URI));
    }

    @Test
    void commence_ShouldIncludeTimestampInResponse() throws ServletException, IOException {
        // Act
        jwtAuthenticationEntryPoint.commence(request, response, authenticationException);
        printWriter.flush();

        // Assert
        String responseBody = stringWriter.toString();
        assertTrue(responseBody.matches(".*\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*"));
    }

    @Test
    void commence_WithDifferentUri_ShouldIncludeCorrectPath() throws ServletException, IOException {
        // Arrange
        String customUri = "/api/admin/dashboard";
        when(request.getRequestURI()).thenReturn(customUri);

        // Act
        jwtAuthenticationEntryPoint.commence(request, response, authenticationException);
        printWriter.flush();

        // Assert
        String responseBody = stringWriter.toString();
        assertTrue(responseBody.contains(customUri));
    }

    @Test
    void commence_WithInsufficientAuthenticationException_ShouldHandleCorrectly() throws ServletException, IOException {
        // Arrange
        AuthenticationException insufficientAuth = new InsufficientAuthenticationException("Token expired");

        // Act
        jwtAuthenticationEntryPoint.commence(request, response, insufficientAuth);
        printWriter.flush();

        // Assert
        String responseBody = stringWriter.toString();
        assertTrue(responseBody.contains(String.valueOf(STATUS_UNAUTHORIZED)));
        verify(response).setStatus(SC_UNAUTHORIZED);
    }

    @Test
    void commence_ShouldCallAllResponseMethods() throws ServletException, IOException {
        // Act
        jwtAuthenticationEntryPoint.commence(request, response, authenticationException);

        // Assert
        verify(response).setStatus(SC_UNAUTHORIZED);
        verify(response).setContentType(APPLICATION_JSON);
        verify(response).setCharacterEncoding(CHARSET_UTF_8);
        verify(response).getWriter();
    }

    @Test
    void commence_WhenWriterThrowsIOException_ShouldPropagateException() throws IOException {
        // Arrange
        when(response.getWriter()).thenThrow(new IOException("Writer error"));

        // Act & Assert
        assertThrows(IOException.class, () ->
                jwtAuthenticationEntryPoint.commence(request, response, authenticationException)
        );
    }

    @Test
    void commence_ShouldVerifyRequestUriIsRetrieved() throws ServletException, IOException {
        // Act
        jwtAuthenticationEntryPoint.commence(request, response, authenticationException);

        // Assert
        verify(request, atLeastOnce()).getRequestURI();
    }

    @Test
    void commence_WithNullExceptionMessage_ShouldStillWork() throws ServletException, IOException {
        // Arrange
        AuthenticationException nullMessageException = new BadCredentialsException(null);

        // Act
        jwtAuthenticationEntryPoint.commence(request, response, nullMessageException);
        printWriter.flush();

        // Assert
        String responseBody = stringWriter.toString();
        assertNotNull(responseBody);
        verify(response).setStatus(SC_UNAUTHORIZED);
    }

    @Test
    void commence_WithEmptyUri_ShouldIncludeEmptyPathInResponse() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("");

        // Act
        jwtAuthenticationEntryPoint.commence(request, response, authenticationException);
        printWriter.flush();

        // Assert
        String responseBody = stringWriter.toString();
        assertNotNull(responseBody);
        verify(response).getWriter();
    }
}