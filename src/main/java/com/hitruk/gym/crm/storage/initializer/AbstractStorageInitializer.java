package com.hitruk.gym.crm.storage.initializer;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
public abstract class AbstractStorageInitializer<T> implements StorageInitializer {

    @Override
    @PostConstruct
    public final void init() {
        log.info("Initializing storage: {}", getClass().getSimpleName());
        List<T> entities = readEntities();
        entities.forEach(this::save);
        log.info("Storage {} initialized with {} entities", getClass().getSimpleName(), entities.size());
    }

    protected abstract List<T> readEntities();

    protected abstract void save(T entity);

    protected List<String> readLines(String filePath) {
        log.debug("Reading data file: {}", filePath);
        try (Stream<String> lines = Files.lines(Path.of(filePath))) {
            return lines.skip(1)
                    .filter(line -> !line.isBlank())
                    .toList();
        } catch (IOException e) {
            log.error("Failed to read data file: {}", filePath, e);
            throw new RuntimeException("Cannot read data file: " + filePath, e);
        }
    }
}
