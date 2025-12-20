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
import org.springframework.security.access.AccessDeniedException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static org.example.security.constants.SecurityConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAccessDeniedHandlerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private JwtAccessDeniedHandler jwtAccessDeniedHandler;

    private StringWriter stringWriter;
    private PrintWriter printWriter;
    private AccessDeniedException accessDeniedException;

    private static final String TEST_URI = "/api/admin/users";
    private static final String TEST_ERROR_MESSAGE = "Insufficient permissions";

    @BeforeEach
    void setUp() throws IOException {
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
        accessDeniedException = new AccessDeniedException(TEST_ERROR_MESSAGE);

        when(response.getWriter()).thenReturn(printWriter);
        when(request.getRequestURI()).thenReturn(TEST_URI);
    }

    @Test
    void handle_ShouldSetCorrectResponseStatus() throws ServletException, IOException {
        // Act
        jwtAccessDeniedHandler.handle(request, response, accessDeniedException);

        // Assert
        verify(response).setStatus(SC_FORBIDDEN);
    }

    @Test
    void handle_ShouldSetCorrectContentType() throws ServletException, IOException {
        // Act
        jwtAccessDeniedHandler.handle(request, response, accessDeniedException);

        // Assert
        verify(response).setContentType(APPLICATION_JSON);
    }

    @Test
    void handle_ShouldSetCorrectCharacterEncoding() throws ServletException, IOException {
        // Act
        jwtAccessDeniedHandler.handle(request, response, accessDeniedException);

        // Assert
        verify(response).setCharacterEncoding(CHARSET_UTF_8);
    }

    @Test
    void handle_ShouldWriteErrorResponseToWriter() throws ServletException, IOException {
        // Act
        jwtAccessDeniedHandler.handle(request, response, accessDeniedException);
        printWriter.flush();

        // Assert
        String responseBody = stringWriter.toString();
        assertNotNull(responseBody);
        assertFalse(responseBody.isEmpty());
        verify(response).getWriter();
    }

    @Test
    void handle_ShouldIncludeStatusInResponse() throws ServletException, IOException {
        // Act
        jwtAccessDeniedHandler.handle(request, response, accessDeniedException);
        printWriter.flush();

        // Assert
        String responseBody = stringWriter.toString();
        assertTrue(responseBody.contains(String.valueOf(STATUS_FORBIDDEN)));
    }

    @Test
    void handle_ShouldIncludeErrorTypeInResponse() throws ServletException, IOException {
        // Act
        jwtAccessDeniedHandler.handle(request, response, accessDeniedException);
        printWriter.flush();

        // Assert
        String responseBody = stringWriter.toString();
        assertTrue(responseBody.contains(ERROR_FORBIDDEN));
    }

    @Test
    void handle_ShouldIncludeErrorMessageInResponse() throws ServletException, IOException {
        // Act
        jwtAccessDeniedHandler.handle(request, response, accessDeniedException);
        printWriter.flush();

        // Assert
        String responseBody = stringWriter.toString();
        assertTrue(responseBody.contains(MESSAGE_FORBIDDEN));
    }

    @Test
    void handle_ShouldIncludeRequestPathInResponse() throws ServletException, IOException {
        // Act
        jwtAccessDeniedHandler.handle(request, response, accessDeniedException);
        printWriter.flush();

        // Assert
        String responseBody = stringWriter.toString();
        assertTrue(responseBody.contains(TEST_URI));
    }

    @Test
    void handle_ShouldIncludeTimestampInResponse() throws ServletException, IOException {
        // Act
        jwtAccessDeniedHandler.handle(request, response, accessDeniedException);
        printWriter.flush();

        // Assert
        String responseBody = stringWriter.toString();
        assertTrue(responseBody.matches(".*\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*"));
    }

    @Test
    void handle_WithDifferentUri_ShouldIncludeCorrectPath() throws ServletException, IOException {
        // Arrange
        String customUri = "/api/protected/resource";
        when(request.getRequestURI()).thenReturn(customUri);

        // Act
        jwtAccessDeniedHandler.handle(request, response, accessDeniedException);
        printWriter.flush();

        // Assert
        String responseBody = stringWriter.toString();
        assertTrue(responseBody.contains(customUri));
    }

    @Test
    void handle_ShouldCallAllResponseMethods() throws ServletException, IOException {
        // Act
        jwtAccessDeniedHandler.handle(request, response, accessDeniedException);

        // Assert
        verify(response).setStatus(SC_FORBIDDEN);
        verify(response).setContentType(APPLICATION_JSON);
        verify(response).setCharacterEncoding(CHARSET_UTF_8);
        verify(response).getWriter();
    }

    @Test
    void handle_WhenWriterThrowsIOException_ShouldPropagateException() throws IOException {
        // Arrange
        when(response.getWriter()).thenThrow(new IOException("Writer error"));

        // Act & Assert
        assertThrows(IOException.class, () ->
                jwtAccessDeniedHandler.handle(request, response, accessDeniedException)
        );
    }

    @Test
    void handle_ShouldVerifyRequestUriIsRetrieved() throws ServletException, IOException {
        // Act
        jwtAccessDeniedHandler.handle(request, response, accessDeniedException);

        // Assert
        verify(request, atLeastOnce()).getRequestURI();
    }
}