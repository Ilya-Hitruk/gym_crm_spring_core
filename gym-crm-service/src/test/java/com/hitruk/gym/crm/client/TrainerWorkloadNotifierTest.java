package com.hitruk.gym.crm.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hitruk.gym.crm.client.dto.ActionType;
import com.hitruk.gym.crm.client.dto.TrainerWorkloadRequest;
import jakarta.jms.Message;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadNotifierTest {

    @Mock
    private JmsTemplate jmsTemplate;

    @Mock
    private Message jmsMessage;

    private TrainerWorkloadNotifier notifier;
    private TrainerWorkloadRequest request;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        notifier = new TrainerWorkloadNotifier(jmsTemplate, objectMapper);

        request = TrainerWorkloadRequest.builder()
                .trainerUsername("trainer1")
                .trainerFirstName("Alex")
                .trainerLastName("Coach")
                .isActive(true)
                .trainingDate(LocalDate.of(2026, 9, 1))
                .trainingDuration(60)
                .actionType(ActionType.ADD)
                .build();
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void notify_validRequest_sendsSerializedPayloadToQueue() {
        ArgumentCaptor<String> payloadCaptor = ArgumentCaptor.forClass(String.class);

        notifier.notify(request);

        verify(jmsTemplate).convertAndSend(eq(TrainerWorkloadNotifier.QUEUE_NAME), payloadCaptor.capture(), any(MessagePostProcessor.class));
        assertTrue(payloadCaptor.getValue().contains("trainer1"));
        assertTrue(payloadCaptor.getValue().contains("ADD"));
    }

    @Test
    void notify_transactionIdInMdc_setsTransactionIdProperty() throws Exception {
        MDC.put(TrainerWorkloadNotifier.TRANSACTION_ID_PROPERTY, "txn-abc-123");
        ArgumentCaptor<MessagePostProcessor> postProcessorCaptor = ArgumentCaptor.forClass(MessagePostProcessor.class);

        notifier.notify(request);

        verify(jmsTemplate).convertAndSend(eq(TrainerWorkloadNotifier.QUEUE_NAME), anyString(), postProcessorCaptor.capture());
        postProcessorCaptor.getValue().postProcessMessage(jmsMessage);
        verify(jmsMessage).setStringProperty(TrainerWorkloadNotifier.TRANSACTION_ID_PROPERTY, "txn-abc-123");
    }

    @Test
    void notify_noTransactionIdInMdc_doesNotSetProperty() throws Exception {
        ArgumentCaptor<MessagePostProcessor> postProcessorCaptor = ArgumentCaptor.forClass(MessagePostProcessor.class);

        notifier.notify(request);

        verify(jmsTemplate).convertAndSend(eq(TrainerWorkloadNotifier.QUEUE_NAME), anyString(), postProcessorCaptor.capture());
        postProcessorCaptor.getValue().postProcessMessage(jmsMessage);
        verifyNoInteractions(jmsMessage);
    }

    @Test
    void notify_brokerUnavailable_doesNotPropagateException() {
        doThrow(new JmsException("connection refused") {
        }).when(jmsTemplate).convertAndSend(anyString(), anyString(), any(MessagePostProcessor.class));

        notifier.notify(request);

        verify(jmsTemplate).convertAndSend(eq(TrainerWorkloadNotifier.QUEUE_NAME), anyString(), any(MessagePostProcessor.class));
    }
}
