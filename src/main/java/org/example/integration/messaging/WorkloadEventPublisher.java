package org.example.integration.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkloadEventPublisher {

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;

    public void publish(Object message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            jmsTemplate.convertAndSend(Queues.WORKLOAD_EVENTS, json);
            log.info("Published JMS message to {} payload={}", Queues.WORKLOAD_EVENTS, json);
        } catch (Exception e) {
            log.error("Failed to publish workload event", e);
            throw new RuntimeException("Failed to publish workload event", e);
        }
    }
}
