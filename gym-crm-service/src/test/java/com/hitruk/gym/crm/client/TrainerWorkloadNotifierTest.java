package com.hitruk.gym.crm.client;

import com.hitruk.gym.crm.client.dto.ActionType;
import com.hitruk.gym.crm.client.dto.TrainerWorkloadRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadNotifierTest {

    @Mock
    private TrainerWorkloadClient trainerWorkloadClient;

    @InjectMocks
    private TrainerWorkloadNotifier notifier;

    @Test
    void notify_delegatesToClient() {
        TrainerWorkloadRequest request = TrainerWorkloadRequest.builder()
                .trainerUsername("trainer1")
                .trainerFirstName("Alex")
                .trainerLastName("Coach")
                .isActive(true)
                .trainingDate(LocalDate.now())
                .trainingDuration(60)
                .actionType(ActionType.ADD)
                .build();

        notifier.notify(request);

        verify(trainerWorkloadClient).updateWorkload(request);
    }
}
