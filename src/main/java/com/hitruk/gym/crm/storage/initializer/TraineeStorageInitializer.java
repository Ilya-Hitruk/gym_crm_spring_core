package com.hitruk.gym.crm.storage.initializer;

import com.hitruk.gym.crm.model.entity.Trainee;
import com.hitruk.gym.crm.storage.ProfileGenerator;
import com.hitruk.gym.crm.storage.TraineeStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class TraineeStorageInitializer extends AbstractStorageInitializer<Trainee> {
    private final TraineeStorage traineeStorage;
    private final ProfileGenerator profileGenerator;
    private final String filePath;

    public TraineeStorageInitializer(TraineeStorage traineeStorage,
                                     ProfileGenerator profileGenerator,
                                     @Value("${storage.trainee.file}") String filePath) {
        this.traineeStorage = traineeStorage;
        this.profileGenerator = profileGenerator;
        this.filePath = filePath;
    }

    @Override
    protected List<Trainee> readEntities() {
        List<String> lines = readLines(filePath);
        List<Trainee> result = new ArrayList<>();

        for (String line : lines) {
            String[] v = line.split(",");

            String firstName = v[1].trim();
            String lastName = v[2].trim();
            String username = profileGenerator.generateUsername(firstName, lastName, result);

            Trainee trainee = Trainee.builder()
                    .id(Long.parseLong(v[0].trim()))
                    .firstName(firstName)
                    .lastName(lastName)
                    .username(username)
                    .password(profileGenerator.generatePassword())
                    .isActive(Boolean.parseBoolean(v[3].trim()))
                    .dateOfBirth(LocalDate.parse(v[4].trim()))
                    .address(v[5].trim())
                    .build();

            log.debug("Parsed trainee: id={}, username={}", trainee.getId(), trainee.getUsername());
            result.add(trainee);
        }
        return result;
    }

    @Override
    protected void save(Trainee entity) {
        traineeStorage.update(entity.getId(), entity);
    }
}
