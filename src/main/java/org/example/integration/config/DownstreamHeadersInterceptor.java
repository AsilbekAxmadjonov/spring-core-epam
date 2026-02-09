package org.example.integration.config;

import org.example.integration.workload.TransactionIdFilter;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.io.IOException;

public class DownstreamHeadersInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {

        String txId = MDC.get(TransactionIdFilter.MDC_KEY);
        if (txId != null && !txId.isBlank()) {
            request.getHeaders().set(TransactionIdFilter.HEADER, txId);
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String tokenValue = null;

        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            tokenValue = jwtAuth.getToken().getTokenValue();
        } else if (auth != null && auth.getCredentials() instanceof String s && !s.isBlank()) {
            tokenValue = s;
        }

        if (tokenValue != null && !tokenValue.isBlank()) {
            request.getHeaders().set(HttpHeaders.AUTHORIZATION, "Bearer " + tokenValue);
        }

        return execution.execute(request, body);
    }
}
