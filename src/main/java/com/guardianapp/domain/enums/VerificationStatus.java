package com.guardianapp.domain.enums;

/**
 * Status of an identity verification request.
 */
public enum VerificationStatus {
    PENDING("Pending", "Waiting for host response"),
    APPROVED("Approved", "Host confirmed the caller identity"),
    REJECTED("Rejected", "Host rejected the caller identity"),
    EXPIRED("Expired", "Verification request expired");

    private final String displayName;
    private final String description;

    VerificationStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isFinalState() {
        return this == APPROVED || this == REJECTED || this == EXPIRED;
    }
}
