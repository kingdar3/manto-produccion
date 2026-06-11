package com.guardianapp.domain.enums;

/**
 * Possible states of a link between a Host and a Protected user.
 */
public enum LinkStatus {
    PENDING("Pending", "Pending link"),
    ACTIVE("Active", "Link confirmed and working"),
    REJECTED("Rejected", "Protected user rejected the request"),
    CANCELLED("Cancelled", "Link was cancelled by one of the parties");

    private final String displayName;
    private final String description;

    LinkStatus(String displayName, String description) {
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
     * Checks if the link is in a terminal state (cannot change).
     */
    public boolean isTerminalState() {
        return this == REJECTED || this == CANCELLED;
    }

    /**
     * Checks if the link allows monitoring operations.
     */
    public boolean allowsMonitoring() {
        return this == ACTIVE;
    }
}
