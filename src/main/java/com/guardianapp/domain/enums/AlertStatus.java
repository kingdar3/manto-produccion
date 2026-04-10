package com.guardianapp.domain.enums;

/**
 * Status of an alert triggered by a potentially dangerous URL.
 */
public enum AlertStatus {
    PENDING("Pending", "Alert is waiting for host resolution"),
    RESOLVED_SAFE("Resolved - Safe", "Host marked URL as safe, access allowed"),
    RESOLVED_BLOCKED("Resolved - Blocked", "Host confirmed URL is dangerous, access blocked");

    private final String displayName;
    private final String description;

    AlertStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Checks if this is a terminal (resolved) state.
     */
    public boolean isResolved() {
        return this == RESOLVED_SAFE || this == RESOLVED_BLOCKED;
    }

    /**
     * Checks if the URL should be allowed based on this status.
     */
    public boolean isUrlAllowed() {
        return this == RESOLVED_SAFE;
    }
}
