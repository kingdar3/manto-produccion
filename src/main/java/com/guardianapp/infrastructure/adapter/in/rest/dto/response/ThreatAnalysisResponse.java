package com.guardianapp.infrastructure.adapter.in.rest.dto.response;

import com.guardianapp.domain.enums.ThreatAnalysisStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for threat analysis.
 */
public record ThreatAnalysisResponse(
    ThreatAnalysisStatus status,
    String source,
    LocalDateTime detectedAt,
    int totalUrls,
    int analyzedUrls,
    int invalidUrls,
    List<UrlThreatAnalysisResponse> urlResults
) {}
