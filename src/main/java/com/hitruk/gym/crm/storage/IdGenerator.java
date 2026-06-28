package com.hitruk.gym.crm.storage;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class IdGenerator {
    private final Map<Class<?>, AtomicLong> sequences = new HashMap<>();

    public Long generate(Class<?> type) {
        return sequences
                .computeIfAbsent(type, key -> new AtomicLong(1))
                .getAndIncrement();
    }

    public void setId(Class<?> type, Long id) {
        sequences.put(type, new AtomicLong(id));
    }
}
