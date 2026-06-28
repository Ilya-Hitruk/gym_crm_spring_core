package com.hitruk.gym.crm.storage.initializer;

import com.hitruk.gym.crm.model.entity.Trainer;
import com.hitruk.gym.crm.model.entity.TrainerSpecialization;
import com.hitruk.gym.crm.storage.ProfileGenerator;
import com.hitruk.gym.crm.storage.TrainerStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class TrainerStorageInitializer extends AbstractStorageInitializer<Trainer> {
    private final TrainerStorage trainerStorage;
    private final ProfileGenerator profileGenerator;
    private final String filePath;

    public TrainerStorageInitializer(TrainerStorage trainerStorage,
                                     ProfileGenerator profileGenerator,
                                     @Value("${storage.trainer.file}") String filePath) {
        this.trainerStorage = trainerStorage;
        this.profileGenerator = profileGenerator;
        this.filePath = filePath;
    }

    @Override
    protected List<Trainer> readEntities() {
        List<String> lines = readLines(filePath);
        List<Trainer> result = new ArrayList<>();

        for (String line : lines) {
            String[] v = line.split(",");

            String firstName = v[1].trim();
            String lastName = v[2].trim();
            String username = profileGenerator.generateUsername(firstName, lastName, result);

            Trainer trainer = Trainer.builder()
                    .id(Long.parseLong(v[0].trim()))
                    .firstName(firstName)
                    .lastName(lastName)
                    .username(username)
                    .password(profileGenerator.generatePassword())
                    .isActive(Boolean.parseBoolean(v[3].trim()))
                    .specialization(TrainerSpecialization.valueOf(v[4].trim()))
                    .build();

            log.debug("Parsed trainer: id={}, username={}", trainer.getId(), trainer.getUsername());
            result.add(trainer);
        }
        return result;
    }

    @Override
    protected void save(Trainer entity) {
        trainerStorage.update(entity.getId(), entity);
    }
}
