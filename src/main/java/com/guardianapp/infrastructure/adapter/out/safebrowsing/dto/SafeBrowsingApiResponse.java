package com.guardianapp.infrastructure.adapter.out.safebrowsing.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Response body for Google Safe Browsing threatMatches.find.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SafeBrowsingApiResponse(
    List<ThreatMatch> matches
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ThreatMatch(
        String threatType,
        String platformType,
        String threatEntryType,
        Threat threat
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Threat(
        String url
    ) {}
}
