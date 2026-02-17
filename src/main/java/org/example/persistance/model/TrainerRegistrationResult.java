package org.example.persistance.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TrainerRegistrationResult {
    String username;
    String temporaryPassword;
    String token;
}
