package org.example.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static org.example.security.constants.SecurityConstants.*;

@Slf4j
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        log.warn("❌ Access denied to: {} - {}",
                request.getRequestURI(),
                accessDeniedException.getMessage());

        response.setStatus(SC_FORBIDDEN);
        response.setContentType(APPLICATION_JSON);
        response.setCharacterEncoding(CHARSET_UTF_8);
        response.getWriter().write(buildErrorResponse(
                STATUS_FORBIDDEN,
                ERROR_FORBIDDEN,
                MESSAGE_FORBIDDEN,
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