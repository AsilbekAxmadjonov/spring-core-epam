package org.example.integration.workload;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.integration.workload.dto.TrainerWorkloadEventRequest;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkloadServiceClient {

    private static final String EVENTS_PATH = "/api/v1/workloads/events";

    private final RestClient workloadRestClient;

    @Retry(name = "workloadRetry")
    @CircuitBreaker(name = "workloadCB", fallbackMethod = "fallbackSendEvent")
    public void sendEvent(String eventId, TrainerWorkloadEventRequest request) {

        workloadRestClient
                .post()
                .uri(EVENTS_PATH)
                .header("X-Event-Id", eventId)
                .body(request)
                .retrieve()
                .toBodilessEntity(); // similar to Void.class exchange
    }

    private void fallbackSendEvent(String eventId, TrainerWorkloadEventRequest request, Throwable ex) {
        log.error("Workload call failed; fallback used. txId={}, eventId={}, event={}",
                MDC.get("transactionId"),
                eventId,
                request,
                ex
        );
    }
}
