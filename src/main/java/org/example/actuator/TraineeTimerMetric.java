package org.example.actuator;

import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class TraineeTimerMetric {

    private final Timer traineeFetchTimer;

    public TraineeTimerMetric(MeterRegistry registry) {
        traineeFetchTimer = Timer.builder("trainee.fetch.time")
                .description("Time taken to fetch trainee")
                .register(registry);
    }

    public <T> T recordFetch(java.util.concurrent.Callable<T> callable) throws Exception {
        return traineeFetchTimer.recordCallable(callable);
    }
}

