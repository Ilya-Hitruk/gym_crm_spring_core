package com.hitruk.gym.workload.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hitruk.gym.workload.api.dto.ActionType;
import com.hitruk.gym.workload.api.dto.TrainerWorkloadRequest;
import com.hitruk.gym.workload.service.TrainerWorkloadService;
import jakarta.jms.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadMessageListenerTest {

    @Mock
    private TrainerWorkloadService trainerWorkloadService;

    @Mock
    private JmsTemplate jmsTemplate;

    @Mock
    private Message jmsMessage;

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

        listener.handleWorkloadMessage(payload, null);

        ArgumentCaptor<TrainerWorkloadRequest> captor = ArgumentCaptor.forClass(TrainerWorkloadRequest.class);
        verify(trainerWorkloadService).processWorkload(captor.capture());
        assertEquals("Alex.Coach", captor.getValue().getTrainerUsername());
        verifyNoInteractions(jmsTemplate);
    }

    @Test
    void handleWorkloadMessage_incomingTransactionId_isVisibleInMdcDuringProcessing() throws Exception {
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
        AtomicReference<String> observedTransactionId = new AtomicReference<>();
        doAnswer(invocation -> {
            observedTransactionId.set(MDC.get(TrainerWorkloadMessageListener.TRANSACTION_ID_PROPERTY));
            return null;
        }).when(trainerWorkloadService).processWorkload(any());

        listener.handleWorkloadMessage(payload, "txn-from-producer");

        assertEquals("txn-from-producer", observedTransactionId.get());
        assertEquals(null, MDC.get(TrainerWorkloadMessageListener.TRANSACTION_ID_PROPERTY));
    }

    @Test
    void handleWorkloadMessage_noIncomingTransactionId_generatesOne() throws Exception {
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
        AtomicReference<String> observedTransactionId = new AtomicReference<>();
        doAnswer(invocation -> {
            observedTransactionId.set(MDC.get(TrainerWorkloadMessageListener.TRANSACTION_ID_PROPERTY));
            return null;
        }).when(trainerWorkloadService).processWorkload(any());

        listener.handleWorkloadMessage(payload, null);

        assertNotNull(observedTransactionId.get());
    }

    @Test
    void handleWorkloadMessage_malformedJson_routesToDeadLetterQueueWithReason() throws Exception {
        ArgumentCaptor<MessagePostProcessor> postProcessorCaptor = ArgumentCaptor.forClass(MessagePostProcessor.class);

        listener.handleWorkloadMessage("not-a-json-payload", "txn-1");

        verify(jmsTemplate).convertAndSend(eq(TrainerWorkloadMessageListener.DEAD_LETTER_QUEUE_NAME),
                eq("not-a-json-payload"), postProcessorCaptor.capture());
        verify(trainerWorkloadService, never()).processWorkload(any());

        postProcessorCaptor.getValue().postProcessMessage(jmsMessage);
        ArgumentCaptor<String> reasonCaptor = ArgumentCaptor.forClass(String.class);
        verify(jmsMessage).setStringProperty(eq(TrainerWorkloadMessageListener.REJECTION_REASON_PROPERTY), reasonCaptor.capture());
        assertTrue(reasonCaptor.getValue().contains("Malformed JSON"));
        verify(jmsMessage).setStringProperty(TrainerWorkloadMessageListener.TRANSACTION_ID_PROPERTY, "txn-1");
    }

    @Test
    void handleWorkloadMessage_missingRequiredField_routesToDeadLetterQueueWithReason() throws Exception {
        TrainerWorkloadRequest request = TrainerWorkloadRequest.builder()
                .trainerFirstName("Alex")
                .trainerLastName("Coach")
                .isActive(true)
                .trainingDate(LocalDate.of(2026, 9, 1))
                .trainingDuration(60)
                .actionType(ActionType.ADD)
                .build();
        String payload = objectMapper.writeValueAsString(request);
        ArgumentCaptor<MessagePostProcessor> postProcessorCaptor = ArgumentCaptor.forClass(MessagePostProcessor.class);

        listener.handleWorkloadMessage(payload, null);

        verify(jmsTemplate).convertAndSend(eq(TrainerWorkloadMessageListener.DEAD_LETTER_QUEUE_NAME),
                eq(payload), postProcessorCaptor.capture());
        verify(trainerWorkloadService, never()).processWorkload(any());

        postProcessorCaptor.getValue().postProcessMessage(jmsMessage);
        ArgumentCaptor<String> reasonCaptor = ArgumentCaptor.forClass(String.class);
        verify(jmsMessage).setStringProperty(eq(TrainerWorkloadMessageListener.REJECTION_REASON_PROPERTY), reasonCaptor.capture());
        assertTrue(reasonCaptor.getValue().contains("trainerUsername"));
    }
}
