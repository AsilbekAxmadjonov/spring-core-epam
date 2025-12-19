package org.example.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MDCInterceptorTest {

    private MDCInterceptor interceptor;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private Object handler;

    @BeforeEach
    void setUp() {
        interceptor = new MDCInterceptor();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        handler = new Object();
        MDC.clear();
    }

    @Test
    void testPreHandleSetsMDCAndResponseHeader() {
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getMethod()).thenReturn("GET");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn("12345");


        boolean result = interceptor.preHandle(request, response, handler);

        assertTrue(result);
        assertNotNull(MDC.get("transactionId"));
        assertEquals("/api/test", MDC.get("requestUri"));
        assertEquals("GET", MDC.get("requestMethod"));
        assertEquals("12345", MDC.get("userId"));

        verify(response, times(1)).setHeader(eq("X-Transaction-Id"), anyString());
    }

    @Test
    void testPreHandleWithNoSession() {
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getMethod()).thenReturn("POST");
        when(request.getSession(false)).thenReturn(null);

        boolean result = interceptor.preHandle(request, response, handler);

        assertTrue(result);
        assertNotNull(MDC.get("transactionId"));
        assertEquals("/api/test", MDC.get("requestUri"));
        assertEquals("POST", MDC.get("requestMethod"));
        assertNull(MDC.get("userId"));
        verify(response, times(1)).setHeader(eq("X-Transaction-Id"), anyString());
    }

    @Test
    void testAfterCompletionClearsMDC() {
        MDC.put("transactionId", "test");
        MDC.put("userId", "123");
        interceptor.afterCompletion(request, response, handler, null);
        assertNull(MDC.get("transactionId"));
        assertNull(MDC.get("userId"));
    }
}
