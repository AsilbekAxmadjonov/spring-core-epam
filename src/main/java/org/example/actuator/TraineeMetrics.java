package org.example.actuator;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class TraineeMetrics {

    private final Counter traineeCreatedCounter;

    public TraineeMetrics(MeterRegistry registry) {
        traineeCreatedCounter = Counter.builder("trainee.created.count")
                .description("Number of trainees created")
                .register(registry);
    }

    public void incrementTraineeCreated() {
        traineeCreatedCounter.increment();
    }
}

