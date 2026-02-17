package org.example.integration.config;

import org.example.integration.workload.TransactionIdFilter;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

public class DownstreamHeadersInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {

        // 1) Forward transaction id
        String txId = MDC.get(TransactionIdFilter.MDC_KEY);
        if (txId != null && !txId.isBlank()) {
            request.getHeaders().set(TransactionIdFilter.HEADER, txId);
        }

        // 2) Forward Authorization token
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String tokenValue = extractToken(auth);

        if (tokenValue != null && !tokenValue.isBlank()) {
            request.getHeaders().set(HttpHeaders.AUTHORIZATION, "Bearer " + tokenValue);
        }

        return execution.execute(request, body);
    }

    private String extractToken(Authentication auth) {
        if (auth == null) return null;

        // Most custom JWT filters can set credentials = raw token
        Object creds = auth.getCredentials();
        if (creds instanceof String s && !s.isBlank() && !"N/A".equalsIgnoreCase(s)) {
            return stripBearerIfPresent(s);
        }

        // Optional fallback if someone stored token in principal as a String
        Object principal = auth.getPrincipal();
        if (principal instanceof String p && !p.isBlank()) {
            return stripBearerIfPresent(p);
        }

        return null;
    }

    private String stripBearerIfPresent(String value) {
        String v = value.trim();
        if (v.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return v.substring(7).trim();
        }
        return v;
    }
}
