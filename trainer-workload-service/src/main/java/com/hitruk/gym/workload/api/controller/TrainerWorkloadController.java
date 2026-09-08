package com.hitruk.gym.workload.api.controller;

import com.hitruk.gym.workload.api.dto.TrainerWorkloadSummaryResponse;
import com.hitruk.gym.workload.service.TrainerWorkloadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/workloads")
@RequiredArgsConstructor
public class TrainerWorkloadController {

    private final TrainerWorkloadService trainerWorkloadService;

    @GetMapping("/{username}")
    public ResponseEntity<TrainerWorkloadSummaryResponse> getWorkload(@PathVariable String username) {
        return ResponseEntity.ok(trainerWorkloadService.getSummary(username));
    }
}
