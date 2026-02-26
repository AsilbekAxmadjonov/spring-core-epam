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

    public void publish(WorkloadEventMessage message) {
        try {
            String json = objectMapper.writeValueAsString(message);

            jmsTemplate.convertAndSend(Queues.WORKLOAD_EVENTS, json);

            log.info(
                    "Published JMS -> queue={} eventId={} txId={}",
                    Queues.WORKLOAD_EVENTS,
                    message.getEventId(),
                    message.getTransactionId()
            );

        } catch (Exception e) {
            log.error("JMS publish failed eventId={}", message.getEventId(), e);
            throw new RuntimeException("Failed to publish workload event", e);
        }
    }
}