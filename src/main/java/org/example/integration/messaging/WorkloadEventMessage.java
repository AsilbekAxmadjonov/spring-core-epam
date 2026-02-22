package org.example.integration.messaging;

import lombok.*;
import org.example.integration.workload.dto.TrainerWorkloadEventRequest;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkloadEventMessage {
    private String eventId;
    private TrainerWorkloadEventRequest request;
}
