package com.hitruk.gym.crm.storage.initializer;

import com.hitruk.gym.crm.model.entity.Training;
import com.hitruk.gym.crm.model.entity.TrainingType;
import com.hitruk.gym.crm.storage.TrainingStorage;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class TrainingStorageInitializer extends AbstractStorageInitializer<Training> {
    private final TrainingStorage trainingStorage;
    private final String filePath;

    public TrainingStorageInitializer(TrainingStorage trainingStorage,
                                      @Value("${storage.training.file}") String filePath) {
        this.trainingStorage = trainingStorage;
        this.filePath = filePath;
    }

    @Override
    protected List<Training> readEntities() {
        return readLines(filePath).stream()
                .map(this::parse)
                .toList();
    }

    private Training parse(String line) {
        String[] v = line.split(",");
        Training training = Training.builder()
                .id(Long.parseLong(v[0].trim()))
                .traineeId(Long.parseLong(v[1].trim()))
                .trainerId(Long.parseLong(v[2].trim()))
                .name(v[3].trim())
                .type(TrainingType.valueOf(v[4].trim()))
                .date(LocalDateTime.parse(v[5].trim()))
                .duration(Duration.parse(v[6].trim()))
                .build();

        log.debug("Parsed training: id={}, name={}", training.getId(), training.getName());
        return training;
    }

    @Override
    protected void save(Training entity) {
        trainingStorage.update(entity.getId(), entity);
    }
}
