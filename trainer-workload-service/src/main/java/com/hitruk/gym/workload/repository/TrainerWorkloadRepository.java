package com.hitruk.gym.workload.repository;

import com.hitruk.gym.workload.document.TrainerWorkloadDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TrainerWorkloadRepository extends MongoRepository<TrainerWorkloadDocument, String> {
}
