package com.guardianapp.domain.enums;

/**
 * Role of a user within a family group.
 */
public enum FamilyMemberRole {
    PRIMARY_HOST,
    SECONDARY_HOST,
    PROTECTED;

    public boolean isHost() {
        return this == PRIMARY_HOST || this == SECONDARY_HOST;
    }
}
