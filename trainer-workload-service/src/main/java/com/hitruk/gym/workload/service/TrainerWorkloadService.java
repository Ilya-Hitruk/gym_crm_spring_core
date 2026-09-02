package com.hitruk.gym.workload.service;

import com.hitruk.gym.workload.api.dto.TrainerWorkloadRequest;
import com.hitruk.gym.workload.api.dto.TrainerWorkloadSummaryResponse;

public interface TrainerWorkloadService {

    void processWorkload(TrainerWorkloadRequest request);

    TrainerWorkloadSummaryResponse getSummary(String username);
}
