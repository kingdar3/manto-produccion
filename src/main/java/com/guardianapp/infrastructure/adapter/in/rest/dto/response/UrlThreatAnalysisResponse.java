package com.guardianapp.infrastructure.adapter.in.rest.dto.response;

import com.guardianapp.domain.enums.UrlThreatStatus;
import com.guardianapp.domain.model.UrlThreatAnalysis;

/**
 * Response DTO for one analyzed URL.
 */
public record UrlThreatAnalysisResponse(
    String url,
    UrlThreatStatus status,
    String reason
) {
    public static UrlThreatAnalysisResponse from(UrlThreatAnalysis analysis) {
        return new UrlThreatAnalysisResponse(
            analysis.url(),
            analysis.status(),
            analysis.reason()
        );
    }
}
