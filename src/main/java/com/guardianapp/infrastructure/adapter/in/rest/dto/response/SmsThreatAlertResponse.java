package com.guardianapp.infrastructure.adapter.in.rest.dto.response;

import com.guardianapp.domain.enums.SmsThreatAlertStatus;
import com.guardianapp.domain.enums.UrlThreatStatus;
import com.guardianapp.domain.model.SmsThreatAlert;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for SMS threat alerts.
 */
public record SmsThreatAlertResponse(
    UUID id,
    UUID linkId,
    UUID protectedUserId,
    UUID hostUserId,
    String sender,
    String messageExcerpt,
    String detectedUrl,
    UrlThreatStatus analysisStatus,
    String analysisReason,
    SmsThreatAlertStatus status,
    boolean urlAllowed,
    LocalDateTime createdAt,
    LocalDateTime resolvedAt,
    UUID resolvedByUserId,
    String resolutionNote,
    long minutesPending
) {
    public static SmsThreatAlertResponse from(SmsThreatAlert alert) {
        return new SmsThreatAlertResponse(
            alert.getId().getValue(),
            alert.getLinkId().getValue(),
            alert.getProtectedUserId().getValue(),
            alert.getHostUserId().getValue(),
            alert.getSender(),
            alert.getMessageExcerpt(),
            alert.getDetectedUrl(),
            alert.getAnalysisStatus(),
            alert.getAnalysisReason(),
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
