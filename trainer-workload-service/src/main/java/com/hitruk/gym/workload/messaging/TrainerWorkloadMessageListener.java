package com.hitruk.gym.workload.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hitruk.gym.workload.api.dto.TrainerWorkloadRequest;
import com.hitruk.gym.workload.service.TrainerWorkloadService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class TrainerWorkloadMessageListener {

    public static final String QUEUE_NAME = "trainer-workload-queue";
    public static final String DEAD_LETTER_QUEUE_NAME = "trainer-workload-dlq";

    private final TrainerWorkloadService trainerWorkloadService;
    private final ObjectMapper objectMapper;
    private final Validator validator;
    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = QUEUE_NAME)
    public void handleWorkloadMessage(String payload) {
        TrainerWorkloadRequest request;
        try {
            request = objectMapper.readValue(payload, TrainerWorkloadRequest.class);
        } catch (Exception e) {
            log.warn("Malformed workload message, routing to DLQ: {}", e.getMessage());
            jmsTemplate.convertAndSend(DEAD_LETTER_QUEUE_NAME, payload);
            return;
        }

        Set<ConstraintViolation<TrainerWorkloadRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            String reasons = violations.stream()
                    .map(v -> v.getPropertyPath() + " " + v.getMessage())
                    .collect(Collectors.joining(", "));
            log.warn("Invalid workload message, routing to DLQ: {}", reasons);
            jmsTemplate.convertAndSend(DEAD_LETTER_QUEUE_NAME, payload);
            return;
        }

        trainerWorkloadService.processWorkload(request);
    }
}
