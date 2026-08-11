package com.hitruk.gym.crm.service;

import com.hitruk.gym.crm.exception.InvalidCredentialsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private TraineeService traineeService;
    @Mock
    private TrainerService trainerService;

    @InjectMocks
    private UserServiceImpl service;

    @Test
    void changePassword_traineeCredentialsMatch_delegatesToTraineeService() {
        when(traineeService.matchCredentials("John.Smith", "old")).thenReturn(true);

        service.changePassword("John.Smith", "old", "new");

        verify(traineeService).changePassword("John.Smith", "old", "new");
        verify(trainerService, never()).changePassword(any(), any(), any());
    }

    @Test
    void changePassword_trainerCredentialsMatch_delegatesToTrainerService() {
        when(traineeService.matchCredentials("Chris.Bumstead", "old")).thenReturn(false);
        when(trainerService.matchCredentials("Chris.Bumstead", "old")).thenReturn(true);

        service.changePassword("Chris.Bumstead", "old", "new");

        verify(trainerService).changePassword("Chris.Bumstead", "old", "new");
    }

    @Test
    void changePassword_noMatch_throwsInvalidCredentialsException() {
        when(traineeService.matchCredentials("Unknown", "old")).thenReturn(false);
        when(trainerService.matchCredentials("Unknown", "old")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class,
                () -> service.changePassword("Unknown", "old", "new"));
    }
}
