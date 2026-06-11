package com.guardianapp.domain.model;

/**
 * Match information for a trusted domain.
 */
public record TrustedDomainMatch(
    String domain,
    String providerName,
    String category
) {}
