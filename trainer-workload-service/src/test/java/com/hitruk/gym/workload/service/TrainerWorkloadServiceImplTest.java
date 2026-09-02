package com.hitruk.gym.workload.service;

import com.hitruk.gym.workload.api.dto.ActionType;
import com.hitruk.gym.workload.api.dto.TrainerWorkloadRequest;
import com.hitruk.gym.workload.api.dto.TrainerWorkloadSummaryResponse;
import com.hitruk.gym.workload.entity.MonthSummary;
import com.hitruk.gym.workload.entity.TrainerWorkload;
import com.hitruk.gym.workload.entity.YearSummary;
import com.hitruk.gym.workload.exception.TrainerWorkloadNotFoundException;
import com.hitruk.gym.workload.repository.TrainerWorkloadRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadServiceImplTest {

    @Mock
    private TrainerWorkloadRepository trainerWorkloadRepository;

    @InjectMocks
    private TrainerWorkloadServiceImpl service;

    private TrainerWorkloadRequest request(LocalDate date, int duration, ActionType actionType) {
        return TrainerWorkloadRequest.builder()
                .trainerUsername("Alex.Coach")
                .trainerFirstName("Alex")
                .trainerLastName("Coach")
                .isActive(true)
                .trainingDate(date)
                .trainingDuration(duration)
                .actionType(actionType)
                .build();
    }

    private TrainerWorkload existingWorkloadWithMonth(int year, int month, int duration) {
        TrainerWorkload workload = TrainerWorkload.builder()
                .username("Alex.Coach").firstName("Alex").lastName("Coach").isActive(true)
                .build();

        YearSummary yearSummary = YearSummary.builder().year(year).trainerWorkload(workload).build();
        MonthSummary monthSummary = MonthSummary.builder()
                .month(month).summaryDuration(duration).yearSummary(yearSummary).build();

        yearSummary.getMonths().add(monthSummary);
        workload.getYears().add(yearSummary);
        return workload;
    }

    @Test
    void processWorkload_newTrainerAdd_createsWorkloadWithDuration() {
        when(trainerWorkloadRepository.findById("Alex.Coach")).thenReturn(Optional.empty());
        ArgumentCaptor<TrainerWorkload> captor = ArgumentCaptor.forClass(TrainerWorkload.class);
        when(trainerWorkloadRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        service.processWorkload(request(LocalDate.of(2026, 9, 1), 60, ActionType.ADD));

        TrainerWorkload saved = captor.getValue();
        assertEquals("Alex.Coach", saved.getUsername());
        assertEquals(1, saved.getYears().size());
        assertEquals(2026, saved.getYears().get(0).getYear());
        assertEquals(1, saved.getYears().get(0).getMonths().size());
        assertEquals(9, saved.getYears().get(0).getMonths().get(0).getMonth());
        assertEquals(60, saved.getYears().get(0).getMonths().get(0).getSummaryDuration());
    }

    @Test
    void processWorkload_secondAddSameMonth_accumulatesDuration() {
        ArgumentCaptor<TrainerWorkload> captor = ArgumentCaptor.forClass(TrainerWorkload.class);
        when(trainerWorkloadRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));
        when(trainerWorkloadRepository.findById("Alex.Coach"))
                .thenReturn(Optional.empty())
                .thenAnswer(inv -> Optional.of(captor.getValue()));

        service.processWorkload(request(LocalDate.of(2026, 9, 1), 60, ActionType.ADD));
        service.processWorkload(request(LocalDate.of(2026, 9, 15), 30, ActionType.ADD));

        TrainerWorkload result = captor.getValue();
        assertEquals(1, result.getYears().size());
        assertEquals(1, result.getYears().get(0).getMonths().size());
        assertEquals(90, result.getYears().get(0).getMonths().get(0).getSummaryDuration());
    }

    @Test
    void processWorkload_addDifferentMonths_createsSeparateEntries() {
        ArgumentCaptor<TrainerWorkload> captor = ArgumentCaptor.forClass(TrainerWorkload.class);
        when(trainerWorkloadRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));
        when(trainerWorkloadRepository.findById("Alex.Coach"))
                .thenReturn(Optional.empty())
                .thenAnswer(inv -> Optional.of(captor.getValue()));

        service.processWorkload(request(LocalDate.of(2026, 9, 1), 60, ActionType.ADD));
        service.processWorkload(request(LocalDate.of(2026, 10, 1), 45, ActionType.ADD));

        TrainerWorkload result = captor.getValue();
        assertEquals(1, result.getYears().size());
        assertEquals(2, result.getYears().get(0).getMonths().size());
    }

    @Test
    void processWorkload_delete_subtractsDuration() {
        TrainerWorkload existing = existingWorkloadWithMonth(2026, 9, 60);
        when(trainerWorkloadRepository.findById("Alex.Coach")).thenReturn(Optional.of(existing));
        when(trainerWorkloadRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.processWorkload(request(LocalDate.of(2026, 9, 1), 20, ActionType.DELETE));

        assertEquals(40, existing.getYears().get(0).getMonths().get(0).getSummaryDuration());
    }

    @Test
    void processWorkload_deleteMoreThanAvailable_neverGoesBelowZero() {
        TrainerWorkload existing = existingWorkloadWithMonth(2026, 9, 40);
        when(trainerWorkloadRepository.findById("Alex.Coach")).thenReturn(Optional.of(existing));
        when(trainerWorkloadRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.processWorkload(request(LocalDate.of(2026, 9, 1), 60, ActionType.DELETE));

        assertEquals(0, existing.getYears().get(0).getMonths().get(0).getSummaryDuration());
    }

    @Test
    void getSummary_unknownTrainer_throwsNotFoundException() {
        when(trainerWorkloadRepository.findById("unknown")).thenReturn(Optional.empty());

        assertThrows(TrainerWorkloadNotFoundException.class, () -> service.getSummary("unknown"));
    }

    @Test
    void getSummary_knownTrainer_returnsMappedResponse() {
        TrainerWorkload existing = existingWorkloadWithMonth(2026, 9, 60);
        when(trainerWorkloadRepository.findById("Alex.Coach")).thenReturn(Optional.of(existing));

        TrainerWorkloadSummaryResponse response = service.getSummary("Alex.Coach");

        assertEquals("Alex.Coach", response.getTrainerUsername());
        assertEquals("Alex", response.getTrainerFirstName());
        assertEquals("Coach", response.getTrainerLastName());
        assertTrue(response.getIsActive());
        assertEquals(1, response.getYears().size());
        assertEquals(2026, response.getYears().get(0).getYear());
        assertEquals(9, response.getYears().get(0).getMonths().get(0).getMonth());
        assertEquals(60, response.getYears().get(0).getMonths().get(0).getSummaryDuration());
    }
}
