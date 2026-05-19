package com.guardianapp.infrastructure.adapter.in.rest.dto.response;

import com.guardianapp.domain.model.BlacklistUrl;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for blacklist URL registration.
 */
public record BlacklistUrlResponse(
    UUID id,
    String url,
    LocalDateTime createdAt
) {
    public static BlacklistUrlResponse from(BlacklistUrl blacklistUrl) {
        return new BlacklistUrlResponse(
            blacklistUrl.id(),
            blacklistUrl.url(),
            blacklistUrl.createdAt()
        );
    }
}
