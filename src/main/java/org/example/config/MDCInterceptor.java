package org.example.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
public class MDCInterceptor implements HandlerInterceptor {

    private static final String TRANSACTION_ID = "transactionId";
    private static final String USER_ID = "userId";
    private static final String REQUEST_URI = "requestUri";
    private static final String REQUEST_METHOD = "requestMethod";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Generate unique transaction ID
        String transactionId = UUID.randomUUID().toString();
        MDC.put(TRANSACTION_ID, transactionId);

        // Add transaction ID to response header for tracing
        response.setHeader("X-Transaction-Id", transactionId);

        // Add request details
        MDC.put(REQUEST_URI, request.getRequestURI());
        MDC.put(REQUEST_METHOD, request.getMethod());

        // Add user ID if available (from session or token)
        String userId = getUserIdFromRequest(request);
        if (userId != null) {
            MDC.put(USER_ID, userId);
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        // Clean up MDC after request completes
        MDC.clear();
    }

    private String getUserIdFromRequest(HttpServletRequest request) {
        // Get user ID from session or JWT token
        Object userId = request.getSession(false) != null
                ? request.getSession().getAttribute("userId")
                : null;
        return userId != null ? userId.toString() : null;
    }
}