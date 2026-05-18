package com.guardianapp.domain.exception;

/**
 * Domain exception for threat analysis flow.
 */
public class ThreatAnalysisException extends DomainException {

    private ThreatAnalysisException(String message, String code) {
        super(message, code);
    }

    private ThreatAnalysisException(String message, String code, Throwable cause) {
        super(message, code, cause);
    }

    public static ThreatAnalysisException invalidRequest(String message) {
        return new ThreatAnalysisException(message, "THREAT_ANALYSIS_INVALID_REQUEST");
    }

    public static ThreatAnalysisException integrationUnavailable() {
        return new ThreatAnalysisException(
            "Safe Browsing integration is disabled or not configured",
            "SAFE_BROWSING_UNAVAILABLE"
        );
    }

    public static ThreatAnalysisException integrationError(String message, Throwable cause) {
        return new ThreatAnalysisException(
            "Safe Browsing integration failed: " + message,
            "SAFE_BROWSING_INTEGRATION_ERROR",
            cause
        );
    }
}
