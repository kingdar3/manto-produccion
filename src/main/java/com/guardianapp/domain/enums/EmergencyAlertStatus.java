package com.guardianapp.domain.enums;

/**
 * Status of an emergency alert.
 */
public enum EmergencyAlertStatus {
    ACTIVE,
    RESOLVED;

    public boolean isResolved() {
        return this == RESOLVED;
    }
}
