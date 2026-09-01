package com.hitruk.gym.crm.security;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenBlacklistService {
    private final Map<String, Instant> blacklist = new ConcurrentHashMap<>();

    public void blacklist(String jti, Instant expiresAt) {
        blacklist.put(jti, expiresAt);
    }

    public boolean isBlackListed(String jti) {
        Instant expiresAt = blacklist.get(jti);
        if (expiresAt == null) {
            return false;
        }

        if (Instant.now().isAfter(expiresAt)) {
            blacklist.remove(jti);
            return false;
        }

        return true;
    }
}
