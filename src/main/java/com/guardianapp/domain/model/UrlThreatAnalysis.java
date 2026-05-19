package com.guardianapp.domain.model;

import com.guardianapp.domain.enums.UrlThreatStatus;
import com.guardianapp.domain.enums.ThreatSignal;

import java.util.List;

/**
 * Result of threat analysis for a single URL.
 */
public record UrlThreatAnalysis(
    String url,
    UrlThreatStatus status,
    String reason,
    int heuristicScore,
    List<ThreatSignal> signals,
    boolean whitelisted,
    String trustedProvider
) {
    public UrlThreatAnalysis {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("URL is required");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status is required");
        }
        if (heuristicScore < 0 || heuristicScore > 100) {
            throw new IllegalArgumentException("Heuristic score must be between 0 and 100");
        }
        if (signals == null) {
            throw new IllegalArgumentException("Signals are required");
        }
    }
}
