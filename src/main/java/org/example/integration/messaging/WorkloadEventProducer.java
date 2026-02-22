package org.example.integration.messaging;

import lombok.RequiredArgsConstructor;
import org.example.integration.workload.dto.TrainerWorkloadEventRequest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkloadEventProducer {

    private final JmsTemplate jmsTemplate;

    public void send(String eventId, TrainerWorkloadEventRequest request) {

        WorkloadEventMessage msg = WorkloadEventMessage.builder()
                .eventId(eventId)
                .request(request)
                .build();

        jmsTemplate.convertAndSend(Queues.WORKLOAD_EVENTS, msg);
    }
}