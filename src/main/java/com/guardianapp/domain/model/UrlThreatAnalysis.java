package com.guardianapp.domain.model;

import com.guardianapp.domain.enums.UrlThreatStatus;

/**
 * Result of threat analysis for a single URL.
 */
public record UrlThreatAnalysis(
    String url,
    UrlThreatStatus status,
    String reason
) {
    public UrlThreatAnalysis {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("URL is required");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status is required");
        }
    }
}
