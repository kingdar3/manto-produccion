package com.guardianapp.infrastructure.adapter.in.rest.dto.response;

import com.guardianapp.domain.enums.AlertStatus;
import com.guardianapp.domain.model.Alert;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for alerts.
 */
public record AlertResponse(
    UUID id,
    UUID linkId,
    UUID protectedUserId,
    String suspiciousUrl,
    String reason,
    AlertStatus status,
    boolean urlAllowed,
    LocalDateTime createdAt,
    LocalDateTime resolvedAt,
    UUID resolvedByUserId,
    String resolutionNote,
    long minutesPending
) {
    /**
     * Creates an AlertResponse from a domain Alert.
     */
    public static AlertResponse from(Alert alert) {
        return new AlertResponse(
            alert.getId().getValue(),
            alert.getLinkId().getValue(),
            alert.getProtectedUserId().getValue(),
            alert.getSuspiciousUrl(),
            alert.getReason(),
            alert.getStatus(),
            alert.isUrlAllowed(),
            alert.getCreatedAt(),
            alert.getResolvedAt(),
            alert.getResolvedByUserId() != null ? alert.getResolvedByUserId().getValue() : null,
            alert.getResolutionNote(),
            alert.isPending() ? alert.getMinutesSinceCreation() : 0
        );
    }
}
