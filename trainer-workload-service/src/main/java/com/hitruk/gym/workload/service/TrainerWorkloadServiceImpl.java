package com.hitruk.gym.workload.service;

import com.hitruk.gym.workload.api.dto.*;
import com.hitruk.gym.workload.entity.*;
import com.hitruk.gym.workload.exception.TrainerWorkloadNotFoundException;
import com.hitruk.gym.workload.repository.TrainerWorkloadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TrainerWorkloadServiceImpl implements TrainerWorkloadService {

    private final TrainerWorkloadRepository trainerWorkloadRepository;

    @Override
    public void processWorkload(TrainerWorkloadRequest request) {
        TrainerWorkload trainerWorkload = trainerWorkloadRepository.findById(request.getTrainerUsername())
                .orElseGet(() -> TrainerWorkload.builder()
                        .username(request.getTrainerUsername())
                        .build());

        trainerWorkload.setFirstName(request.getTrainerFirstName());
        trainerWorkload.setLastName(request.getTrainerLastName());
        trainerWorkload.setIsActive(request.getIsActive());

        LocalDate date = request.getTrainingDate();
        YearSummary yearSummary = findOrCreateYear(trainerWorkload, date.getYear());
        MonthSummary monthSummary = findOrCreateMonth(yearSummary, date.getMonthValue());

        int delta = request.getActionType() == ActionType.ADD
                ? request.getTrainingDuration()
                : -request.getTrainingDuration();

        monthSummary.setSummaryDuration(Math.max(monthSummary.getSummaryDuration() + delta, 0));

        trainerWorkloadRepository.save(trainerWorkload);

        log.info("Workload {} processed for trainer={}, {}-{}: durationDelta={} min, monthTotal={} min",
                request.getActionType(), request.getTrainerUsername(), date.getYear(), date.getMonthValue(),
                delta, monthSummary.getSummaryDuration());
    }

    @Override
    public TrainerWorkloadSummaryResponse getSummary(String username) {
        TrainerWorkload trainerWorkload = trainerWorkloadRepository.findById(username)
                .orElseThrow(() -> new TrainerWorkloadNotFoundException("No workload data for trainer: " + username));
        return toDto(trainerWorkload);
    }

    private YearSummary findOrCreateYear(TrainerWorkload trainerWorkload, int year) {
        return trainerWorkload.getYears().stream()
                .filter(y -> y.getYear() == year)
                .findFirst()
                .orElseGet(() -> {
                    YearSummary created = YearSummary.builder()
                            .year(year)
                            .trainerWorkload(trainerWorkload)
                            .build();
                    trainerWorkload.getYears().add(created);
                    return created;
                });
    }

    private MonthSummary findOrCreateMonth(YearSummary yearSummary, int month) {
        return yearSummary.getMonths().stream()
                .filter(m -> m.getMonth() == month)
                .findFirst()
                .orElseGet(() -> {
                    MonthSummary created = MonthSummary.builder()
                            .month(month)
                            .summaryDuration(0)
                            .yearSummary(yearSummary)
                            .build();
                    yearSummary.getMonths().add(created);
                    return created;
                });
    }

    private TrainerWorkloadSummaryResponse toDto(TrainerWorkload trainerWorkload) {
        List<YearSummaryDto> years = trainerWorkload.getYears().stream()
                .map(y -> YearSummaryDto.builder()
                        .year(y.getYear())
                        .months(y.getMonths().stream()
                                .map(m -> MonthSummaryDto.builder()
                                        .month(m.getMonth())
                                        .summaryDuration(m.getSummaryDuration())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());

        return TrainerWorkloadSummaryResponse.builder()
                .trainerUsername(trainerWorkload.getUsername())
                .trainerFirstName(trainerWorkload.getFirstName())
                .trainerLastName(trainerWorkload.getLastName())
                .isActive(trainerWorkload.getIsActive())
                .years(years)
                .build();
    }
}