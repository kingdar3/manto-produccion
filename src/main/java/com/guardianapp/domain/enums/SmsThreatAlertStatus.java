package com.guardianapp.domain.enums;

/**
 * Lifecycle status for SMS threat alerts.
 */
public enum SmsThreatAlertStatus {
    PENDING,
    RESOLVED_SAFE,
    RESOLVED_BLOCKED;

    public boolean isResolved() {
        return this == RESOLVED_SAFE || this == RESOLVED_BLOCKED;
    }

    public boolean isUrlAllowed() {
        return this == RESOLVED_SAFE;
    }
}
