package org.example.integration.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkloadEventPublisher {

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public void publish(WorkloadEventMessage message) {

        String json = objectMapper.writeValueAsString(message);

        jmsTemplate.convertAndSend(Queues.WORKLOAD_EVENTS, json);

        log.info(
                "Published JMS -> queue={} eventId={} txId={}",
                Queues.WORKLOAD_EVENTS,
                message.getEventId(),
                message.getTransactionId()
        );
    }
}