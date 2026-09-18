package com.hitruk.gym.crm.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hitruk.gym.crm.client.dto.TrainerWorkloadRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class TrainerWorkloadNotifier {

    public static final String QUEUE_NAME = "trainer-workload-queue";
    public static final String TRANSACTION_ID_PROPERTY = "transactionId";

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;

    public void notify(TrainerWorkloadRequest request) {
        try {
            String payload = objectMapper.writeValueAsString(request);
            String transactionId = MDC.get(TRANSACTION_ID_PROPERTY);
            jmsTemplate.convertAndSend(QUEUE_NAME, payload, message -> {
                if (transactionId != null) {
                    message.setStringProperty(TRANSACTION_ID_PROPERTY, transactionId);
                }
                return message;
            });
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize workload message for trainer={}: {}",
                    request.getTrainerUsername(), e.getMessage());
        } catch (JmsException e) {
            log.warn("trainer-workload-service unreachable, skipping workload update: trainer={}, action={}, reason={}",
                    request.getTrainerUsername(), request.getActionType(), e.getMessage());
        }
    }
}
