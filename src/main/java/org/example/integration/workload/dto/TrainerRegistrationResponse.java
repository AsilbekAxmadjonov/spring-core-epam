package org.example.integration.workload.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrainerRegistrationResponse {
    private String username;
    private String temporaryPassword;
    private String token;
}
