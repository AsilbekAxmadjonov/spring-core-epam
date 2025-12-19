package org.example.actuator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class DatabaseHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        boolean databaseUp = checkDatabase(); // replace with real DB check

        if (databaseUp) {
            return Health.up().withDetail("database", "PostgreSQL is running").build();
        } else {
            return Health.down().withDetail("database", "PostgreSQL is DOWN").build();
        }
    }

    private boolean checkDatabase() {
        return true;
    }
}

