package com.hitruk.gym.crm.util;

import com.hitruk.gym.crm.repository.TrainingTypeRepository;
import com.hitruk.gym.crm.model.entity.TrainingType;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DataInitializer {
    private final TrainingTypeRepository trainingTypeRepository;
    private final PlatformTransactionManager transactionManager;

    @Autowired
    public DataInitializer(TrainingTypeRepository trainingTypeRepository,
                           PlatformTransactionManager transactionManager) {
        this.trainingTypeRepository = trainingTypeRepository;
        this.transactionManager = transactionManager;
    }

    @PostConstruct
    public void init() {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.execute(status -> {
            List<String> defaults = List.of("FITNESS", "YOGA", "STRENGTH", "CARDIO");
            Set<String> existing = trainingTypeRepository.findAll().stream()
                    .map(TrainingType::getName)
                    .collect(Collectors.toSet());
            defaults.stream()
                    .filter(name -> !existing.contains(name))
                    .map(name -> TrainingType.builder().name(name).build())
                    .forEach(trainingTypeRepository::save);
            log.info("Training types initialized");
            return null;
        });
    }
}
