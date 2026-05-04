package com.appointment.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class LoginAttemptService {

    private final Cache<String, Integer> attemptsCache;

    public LoginAttemptService() {
        this.attemptsCache = Caffeine.newBuilder()
                .expireAfterWrite(15, TimeUnit.MINUTES)
                .maximumSize(100)
                .build();
    }

    public void loginSucceeded(String key) {
        attemptsCache.invalidate(key);
    }

    public void loginFailed(String key) {
        int attempts = attemptsCache.get(key, k -> 0);
        attemptsCache.put(key, attempts + 1);
    }

    public boolean isBlocked(String key) {
        Integer attempts = attemptsCache.getIfPresent(key);
        return attempts != null && attempts >= 5;
    }
}