package com.guardianapp.infrastructure.adapter.in.rest.dto.response;

import com.guardianapp.domain.enums.ThreatSignal;
import com.guardianapp.domain.enums.UrlThreatStatus;
import com.guardianapp.domain.model.ThreatAnalysisResult;
import com.guardianapp.domain.model.UrlThreatAnalysis;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for single URL analysis.
 */
public record AnalyzeSingleUrlResponse(
    String url,
    UrlThreatStatus status,
    String reason,
    int heuristicScore,
    List<ThreatSignal> signals,
    boolean whitelisted,
    String trustedProvider,
    String source,
    LocalDateTime detectedAt
) {
    public static AnalyzeSingleUrlResponse from(ThreatAnalysisResult result) {
        UrlThreatAnalysis first = result.urlResults().getFirst();
        return new AnalyzeSingleUrlResponse(
            first.url(),
            first.status(),
            first.reason(),
            first.heuristicScore(),
            first.signals(),
            first.whitelisted(),
            first.trustedProvider(),
            result.source(),
            result.detectedAt()
        );
    }
}
