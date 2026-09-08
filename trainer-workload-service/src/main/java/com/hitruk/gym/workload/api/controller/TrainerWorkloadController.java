package com.hitruk.gym.workload.api.controller;

import com.hitruk.gym.workload.api.dto.TrainerWorkloadRequest;
import com.hitruk.gym.workload.api.dto.TrainerWorkloadSummaryResponse;
import com.hitruk.gym.workload.service.TrainerWorkloadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/workloads")
@RequiredArgsConstructor
public class TrainerWorkloadController {

    private final TrainerWorkloadService trainerWorkloadService;

    @PostMapping
    public ResponseEntity<Void> updateWorkload(@Valid @RequestBody TrainerWorkloadRequest request) {
        trainerWorkloadService.processWorkload(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}")
    public ResponseEntity<TrainerWorkloadSummaryResponse> getWorkload(@PathVariable String username) {
        return ResponseEntity.ok(trainerWorkloadService.getSummary(username));
    }
}
