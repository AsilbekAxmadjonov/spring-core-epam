package org.example.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.example.security.constants.SecurityConstants.*;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        log.warn("❌ Unauthorized access attempt to: {} - {}",
                request.getRequestURI(),
                authException.getMessage());

        response.setStatus(SC_UNAUTHORIZED);
        response.setContentType(APPLICATION_JSON);
        response.setCharacterEncoding(CHARSET_UTF_8);
        response.getWriter().write(buildErrorResponse(
                STATUS_UNAUTHORIZED,
                ERROR_UNAUTHORIZED,
                MESSAGE_UNAUTHORIZED,
                request.getRequestURI()
        ));
    }

    private String buildErrorResponse(int status, String error, String message, String path) {
        return String.format(
                ERROR_RESPONSE_TEMPLATE,
                LocalDateTime.now(),
                status,
                error,
                message,
                path
        );
    }
}