package com.guardianapp.infrastructure.adapter.in.rest.dto.response;

import com.guardianapp.domain.enums.UrlThreatStatus;
import com.guardianapp.domain.enums.ThreatSignal;
import com.guardianapp.domain.model.UrlThreatAnalysis;

import java.util.List;

/**
 * Response DTO for one analyzed URL.
 */
public record UrlThreatAnalysisResponse(
    String url,
    UrlThreatStatus status,
    String reason,
    int heuristicScore,
    List<ThreatSignal> signals,
    boolean whitelisted,
    String trustedProvider
) {
    public static UrlThreatAnalysisResponse from(UrlThreatAnalysis analysis) {
        return new UrlThreatAnalysisResponse(
            analysis.url(),
            analysis.status(),
            analysis.reason(),
            analysis.heuristicScore(),
            analysis.signals(),
            analysis.whitelisted(),
            analysis.trustedProvider()
        );
    }
}
