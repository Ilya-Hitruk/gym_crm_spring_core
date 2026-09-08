package com.hitruk.gym.workload.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hitruk.gym.workload.api.dto.ActionType;
import com.hitruk.gym.workload.api.dto.TrainerWorkloadRequest;
import com.hitruk.gym.workload.service.TrainerWorkloadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadMessageListenerTest {

    @Mock
    private TrainerWorkloadService trainerWorkloadService;

    @Mock
    private JmsTemplate jmsTemplate;

    private TrainerWorkloadMessageListener listener;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        listener = new TrainerWorkloadMessageListener(trainerWorkloadService, objectMapper, validator, jmsTemplate);
    }

    @Test
    void handleWorkloadMessage_validPayload_processesWorkload() throws Exception {
        TrainerWorkloadRequest request = TrainerWorkloadRequest.builder()
                .trainerUsername("Alex.Coach")
                .trainerFirstName("Alex")
                .trainerLastName("Coach")
                .isActive(true)
                .trainingDate(LocalDate.of(2026, 9, 1))
                .trainingDuration(60)
                .actionType(ActionType.ADD)
                .build();
        String payload = objectMapper.writeValueAsString(request);

        listener.handleWorkloadMessage(payload);

        ArgumentCaptor<TrainerWorkloadRequest> captor = ArgumentCaptor.forClass(TrainerWorkloadRequest.class);
        verify(trainerWorkloadService).processWorkload(captor.capture());
        assertEquals("Alex.Coach", captor.getValue().getTrainerUsername());
        verifyNoInteractions(jmsTemplate);
    }

    @Test
    void handleWorkloadMessage_malformedJson_routesToDeadLetterQueue() {
        listener.handleWorkloadMessage("not-a-json-payload");

        verify(jmsTemplate).convertAndSend(eq(TrainerWorkloadMessageListener.DEAD_LETTER_QUEUE_NAME), eq("not-a-json-payload"));
        verify(trainerWorkloadService, never()).processWorkload(any());
    }

    @Test
    void handleWorkloadMessage_missingRequiredField_routesToDeadLetterQueue() throws Exception {
        TrainerWorkloadRequest request = TrainerWorkloadRequest.builder()
                .trainerFirstName("Alex")
                .trainerLastName("Coach")
                .isActive(true)
                .trainingDate(LocalDate.of(2026, 9, 1))
                .trainingDuration(60)
                .actionType(ActionType.ADD)
                .build();
        String payload = objectMapper.writeValueAsString(request);

        listener.handleWorkloadMessage(payload);

        verify(jmsTemplate).convertAndSend(eq(TrainerWorkloadMessageListener.DEAD_LETTER_QUEUE_NAME), eq(payload));
        verify(trainerWorkloadService, never()).processWorkload(any());
    }
}
