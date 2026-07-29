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
    void matchCredentials_traineeMatches_returnsTrue() {
        when(traineeService.matchCredentials("John.Smith", "pass")).thenReturn(true);

        assertTrue(service.matchCredentials("John.Smith", "pass"));
        verify(trainerService, never()).matchCredentials(any(), any());
    }

    @Test
    void matchCredentials_trainerMatches_returnsTrue() {
        when(traineeService.matchCredentials("Chris.Bumstead", "pass")).thenReturn(false);
        when(trainerService.matchCredentials("Chris.Bumstead", "pass")).thenReturn(true);

        assertTrue(service.matchCredentials("Chris.Bumstead", "pass"));
    }

    @Test
    void matchCredentials_neitherMatches_returnsFalse() {
        when(traineeService.matchCredentials("Unknown", "pass")).thenReturn(false);
        when(trainerService.matchCredentials("Unknown", "pass")).thenReturn(false);

        assertFalse(service.matchCredentials("Unknown", "pass"));
    }

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
