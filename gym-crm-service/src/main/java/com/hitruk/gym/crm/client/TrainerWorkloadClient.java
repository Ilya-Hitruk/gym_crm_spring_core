package com.hitruk.gym.crm.client;

import com.hitruk.gym.crm.client.dto.TrainerWorkloadRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "trainer-workload-service")
public interface TrainerWorkloadClient {

    @PostMapping("/api/v1/workloads")
    void updateWorkload(@RequestBody TrainerWorkloadRequest request);
}
