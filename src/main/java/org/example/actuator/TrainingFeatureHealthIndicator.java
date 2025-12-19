package org.example.actuator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class TrainingFeatureHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        return Health.up().withDetail("training-service", "Available").build();
    }
}

