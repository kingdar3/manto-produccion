package com.guardianapp.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model for a blacklisted URL.
 */
public record BlacklistUrl(
    UUID id,
    String url,
    LocalDateTime createdAt
) {
    public BlacklistUrl {
        if (id == null) {
            throw new IllegalArgumentException("Blacklist URL id is required");
        }
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("URL is required");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("CreatedAt is required");
        }
    }
}
