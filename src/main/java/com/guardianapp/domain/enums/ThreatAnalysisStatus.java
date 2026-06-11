package com.guardianapp.domain.enums;

/**
 * Global status for a threat analysis request.
 */
public enum ThreatAnalysisStatus {
    FRAUD_RISK,
    MALWARE_RISK,
    UNWANTED_RISK,
    SUSPICIOUS_RISK,
    NO_RISK_DETECTED,
    PARTIAL_ANALYSIS,
    ANALYSIS_ERROR
}
