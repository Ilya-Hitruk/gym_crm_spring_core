package com.hitruk.gym.crm.client;

import com.hitruk.gym.crm.client.dto.TrainerWorkloadRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class TrainerWorkloadNotifier {

    private final TrainerWorkloadClient trainerWorkloadClient;

    @CircuitBreaker(name = "trainerWorkload", fallbackMethod = "fallback")
    public void notify(TrainerWorkloadRequest request) {
        trainerWorkloadClient.updateWorkload(request);
    }

    private void fallback(TrainerWorkloadRequest request, Throwable ex) {
        log.warn("trainer-workload-service unavailable, skipping workload update: trainer={}, action={}, reason={}",
                request.getTrainerUsername(), request.getActionType(), ex.getMessage());
    }
}
