package com.guardianapp.infrastructure.adapter.in.rest.dto.response;

import com.guardianapp.domain.enums.LinkStatus;

import java.time.LocalDateTime;

/**
 * Response DTO for link data.
 */
public record LinkResponse(
    String id,
    String hostId,
    String protectedId,
    LinkStatus status,
    String connectionCode,
    LocalDateTime codeExpiresAt,
    long remainingMinutes,
    LocalDateTime createdAt,
    LocalDateTime confirmedAt
) {}
