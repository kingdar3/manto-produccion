package com.guardianapp.infrastructure.adapter.in.rest.dto.response;

import com.guardianapp.domain.enums.InvitationStatus;

import java.time.LocalDateTime;

/**
 * Response DTO for invitation data.
 */
public record InvitationResponse(
    String id,
    String hostId,
    String hostName,
    String token,
    String shareableLink,
    InvitationStatus status,
    LocalDateTime expiresAt,
    long remainingMinutes,
    LocalDateTime createdAt,
    LocalDateTime acceptedAt,
    String acceptedByUserId
) {}
