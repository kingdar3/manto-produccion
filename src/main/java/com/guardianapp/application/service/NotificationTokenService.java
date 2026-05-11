package com.guardianapp.application.service;

import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.domain.port.out.DeviceTokenRepositoryPort;

/**
 * Application service for registering mobile push tokens.
 */
public class NotificationTokenService {

    private final DeviceTokenRepositoryPort repository;

    public NotificationTokenService(DeviceTokenRepositoryPort repository) {
        this.repository = repository;
    }

    public void register(UserId userId, String token, String platform) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token is required");
        }
        String normalizedPlatform = (platform == null || platform.isBlank()) ? "android" : platform;
        repository.saveOrUpdate(userId, token, normalizedPlatform);
    }
}
