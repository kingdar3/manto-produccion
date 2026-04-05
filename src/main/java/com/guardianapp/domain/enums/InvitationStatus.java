package com.guardianapp.domain.enums;

/**
 * Status of an invitation to create a link.
 */
public enum InvitationStatus {
    PENDING("Pending", "Invitation is waiting to be accepted"),
    ACCEPTED("Accepted", "Invitation has been accepted and link created"),
    EXPIRED("Expired", "Invitation has expired"),
    CANCELLED("Cancelled", "Invitation was cancelled by the host");

    private final String displayName;
    private final String description;

    InvitationStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
