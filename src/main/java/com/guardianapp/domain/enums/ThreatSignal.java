package com.guardianapp.domain.enums;

/**
 * Heuristic signals used to complement Safe Browsing detection.
 */
public enum ThreatSignal {
    TRUSTED_WHITELIST,
    URGENCY_LANGUAGE,
    CREDENTIAL_REQUEST,
    ACCOUNT_THREAT_LANGUAGE,
    SUSPICIOUS_TLD,
    URL_SHORTENER,
    IP_HOST,
    PUNYCODE_DOMAIN,
    MANY_HYPHENS,
    EXCESSIVE_SUBDOMAINS,
    NON_HTTPS,
    BRAND_DOMAIN_MISMATCH
}
