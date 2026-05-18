package com.guardianapp.domain.model;

import com.guardianapp.domain.enums.ThreatAnalysisStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Global result for a threat analysis request.
 */
public record ThreatAnalysisResult(
    ThreatAnalysisStatus status,
    List<UrlThreatAnalysis> urlResults,
    String source,
    LocalDateTime detectedAt,
    int totalUrls,
    int analyzedUrls,
    int invalidUrls
) {
    public ThreatAnalysisResult {
        if (status == null) {
            throw new IllegalArgumentException("Global status is required");
        }
        if (urlResults == null) {
            throw new IllegalArgumentException("URL results are required");
        }
        if (source == null || source.isBlank()) {
            throw new IllegalArgumentException("Source is required");
        }
        if (detectedAt == null) {
            throw new IllegalArgumentException("Detection time is required");
        }
    }
}
