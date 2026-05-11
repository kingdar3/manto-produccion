package com.guardianapp.domain.model;

import com.guardianapp.domain.enums.FamilyMemberRole;
import com.guardianapp.domain.model.valueobject.UserId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Member of a family group.
 */
public class FamilyGroupMember {

    private final UserId userId;
    private FamilyMemberRole role;
    private final LocalDateTime joinedAt;

    private FamilyGroupMember(UserId userId, FamilyMemberRole role, LocalDateTime joinedAt) {
        this.userId = Objects.requireNonNull(userId, "User ID is required");
        this.role = Objects.requireNonNull(role, "Role is required");
        this.joinedAt = Objects.requireNonNull(joinedAt, "Joined at is required");
    }

    public static FamilyGroupMember create(UserId userId, FamilyMemberRole role) {
        return new FamilyGroupMember(userId, role, LocalDateTime.now());
    }

    public static FamilyGroupMember reconstitute(UserId userId, FamilyMemberRole role, LocalDateTime joinedAt) {
        return new FamilyGroupMember(userId, role, joinedAt);
    }

    public UserId getUserId() {
        return userId;
    }

    public FamilyMemberRole getRole() {
        return role;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public boolean isPrimaryHost() {
        return role == FamilyMemberRole.PRIMARY_HOST;
    }

    public boolean isHost() {
        return role.isHost();
    }

    public void promoteToPrimaryHost() {
        this.role = FamilyMemberRole.PRIMARY_HOST;
    }

    public void demoteToSecondaryHost() {
        this.role = FamilyMemberRole.SECONDARY_HOST;
    }
}
