package org.example.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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
        String transactionId = UUID.randomUUID().toString();
        MDC.put(TRANSACTION_ID, transactionId);

        response.setHeader("X-Transaction-Id", transactionId);

        MDC.put(REQUEST_URI, request.getRequestURI());
        MDC.put(REQUEST_METHOD, request.getMethod());

        String userId = getUserIdFromRequest(request);
        if (userId != null) {
            MDC.put(USER_ID, userId);
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        MDC.clear();
    }

    private String getUserIdFromRequest(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object userId = session.getAttribute("userId");
            if (userId != null) return userId.toString();
        }
        return null;
    }

}