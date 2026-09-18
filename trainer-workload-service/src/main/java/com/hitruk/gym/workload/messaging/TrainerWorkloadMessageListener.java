package com.hitruk.gym.workload.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hitruk.gym.workload.api.dto.TrainerWorkloadRequest;
import com.hitruk.gym.workload.service.TrainerWorkloadService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class TrainerWorkloadMessageListener {

    public static final String QUEUE_NAME = "trainer-workload-queue";
    public static final String DEAD_LETTER_QUEUE_NAME = "trainer-workload-dlq";
    public static final String TRANSACTION_ID_PROPERTY = "transactionId";
    public static final String REJECTION_REASON_PROPERTY = "rejectionReason";

    private final TrainerWorkloadService trainerWorkloadService;
    private final ObjectMapper objectMapper;
    private final Validator validator;
    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = QUEUE_NAME)
    public void handleWorkloadMessage(@Payload String payload,
                                       @Header(value = TRANSACTION_ID_PROPERTY, required = false) String incomingTransactionId) {
        String transactionId = incomingTransactionId != null ? incomingTransactionId : UUID.randomUUID().toString();
        try {
            MDC.put(TRANSACTION_ID_PROPERTY, transactionId);

            TrainerWorkloadRequest request;
            try {
                request = objectMapper.readValue(payload, TrainerWorkloadRequest.class);
            } catch (Exception e) {
                routeToDeadLetterQueue(payload, "Malformed JSON: " + e.getMessage(), transactionId);
                return;
            }

            Set<ConstraintViolation<TrainerWorkloadRequest>> violations = validator.validate(request);
            if (!violations.isEmpty()) {
                String reasons = violations.stream()
                        .map(v -> v.getPropertyPath() + " " + v.getMessage())
                        .collect(Collectors.joining(", "));
                routeToDeadLetterQueue(payload, "Validation failed: " + reasons, transactionId);
                return;
            }

            trainerWorkloadService.processWorkload(request);
        } finally {
            MDC.remove(TRANSACTION_ID_PROPERTY);
        }
    }

    private void routeToDeadLetterQueue(String payload, String reason, String transactionId) {
        log.warn("Routing message to DLQ: {}", reason);
        jmsTemplate.convertAndSend(DEAD_LETTER_QUEUE_NAME, payload, message -> {
            message.setStringProperty(REJECTION_REASON_PROPERTY, reason);
            message.setStringProperty(TRANSACTION_ID_PROPERTY, transactionId);
            return message;
        });
    }
}
