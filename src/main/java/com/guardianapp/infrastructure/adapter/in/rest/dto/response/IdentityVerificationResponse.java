package com.guardianapp.infrastructure.adapter.in.rest.dto.response;

import com.guardianapp.domain.enums.VerificationStatus;
import com.guardianapp.domain.model.IdentityVerification;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for identity verification.
 */
public record IdentityVerificationResponse(
    UUID id,
    UUID linkId,
    UUID protectedUserId,
    UUID hostUserId,
    String claimedPerson,
    String challengeCode,
    VerificationStatus status,
    LocalDateTime createdAt,
    LocalDateTime expiresAt,
    LocalDateTime resolvedAt,
    String resolutionNote,
    long remainingSeconds
) {
    public static IdentityVerificationResponse from(IdentityVerification verification) {
        return new IdentityVerificationResponse(
            verification.getId().getValue(),
            verification.getLinkId().getValue(),
            verification.getProtectedUserId().getValue(),
            verification.getHostUserId().getValue(),
            verification.getClaimedPerson(),
            verification.getChallengeCode(),
            verification.getStatus(),
            verification.getCreatedAt(),
            verification.getExpiresAt(),
            verification.getResolvedAt(),
            verification.getResolutionNote(),
            verification.isPending() ? verification.getRemainingSeconds() : 0
        );
    }
}
