package com.guardianapp.infrastructure.adapter.in.rest.dto.request;

import com.guardianapp.domain.enums.UrlThreatStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Request DTO for creating SMS threat alerts and SMS history records.
 */
public record CreateSmsThreatAlertRequest(
    @NotNull(message = "Link ID is required")
    UUID linkId,

    @NotNull(message = "Protected user ID is required")
    UUID protectedUserId,

    @NotBlank(message = "Sender is required")
    @Size(max = 160, message = "Sender must not exceed 160 characters")
    String sender,

    @NotBlank(message = "Message excerpt is required")
    @Size(max = 5000, message = "Message excerpt must not exceed 5000 characters")
    String messageExcerpt,

    @Size(max = 2048, message = "Detected URL must not exceed 2048 characters")
    String detectedUrl,

    @NotNull(message = "Analysis status is required")
    UrlThreatStatus analysisStatus,

    @Size(max = 1000, message = "Analysis reason must not exceed 1000 characters")
    String analysisReason
) {}
