package com.guardianapp.domain.model;

import com.guardianapp.domain.enums.FamilyMemberRole;
import com.guardianapp.domain.model.valueobject.FamilyGroupId;
import com.guardianapp.domain.model.valueobject.UserId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Aggregate root representing a family circle.
 */
public class FamilyGroup {

    private static final int MAX_MEMBERS = 4;
    private static final int MAX_SECONDARY_HOSTS = 5;

    private final FamilyGroupId id;
    private String name;
    private final UserId primaryHostUserId;
    private final LocalDateTime createdAt;
    private final List<FamilyGroupMember> members;

    private FamilyGroup(
            FamilyGroupId id,
            String name,
            UserId primaryHostUserId,
            LocalDateTime createdAt,
            List<FamilyGroupMember> members) {
        this.id = Objects.requireNonNull(id, "Family group ID is required");
        this.name = validateName(name);
        this.primaryHostUserId = Objects.requireNonNull(primaryHostUserId, "Primary host is required");
        this.createdAt = Objects.requireNonNull(createdAt, "Created at is required");
        this.members = new ArrayList<>(Objects.requireNonNull(members, "Members list is required"));
    }

    public static FamilyGroup create(String name, UserId primaryHostUserId) {
        FamilyGroupMember owner = FamilyGroupMember.create(primaryHostUserId, FamilyMemberRole.PRIMARY_HOST);
        List<FamilyGroupMember> members = new ArrayList<>();
        members.add(owner);
        return new FamilyGroup(
                FamilyGroupId.generate(),
                name,
                primaryHostUserId,
                LocalDateTime.now(),
                members
        );
    }

    public static FamilyGroup reconstitute(
            FamilyGroupId id,
            String name,
            UserId primaryHostUserId,
            LocalDateTime createdAt,
            List<FamilyGroupMember> members) {
        return new FamilyGroup(id, name, primaryHostUserId, createdAt, members);
    }

    public void addProtectedMember(UserId requesterId, UserId protectedUserId) {
        ensurePrimaryHost(requesterId);
        ensureMemberNotExists(protectedUserId);
        ensureMemberLimitNotReached();
        members.add(FamilyGroupMember.create(protectedUserId, FamilyMemberRole.PROTECTED));
    }

    public void addSecondaryHost(UserId requesterId, UserId hostUserId) {
        ensurePrimaryHost(requesterId);
        ensureMemberNotExists(hostUserId);
        ensureMemberLimitNotReached();

        long secondaryHosts = members.stream()
                .filter(m -> m.getRole() == FamilyMemberRole.SECONDARY_HOST)
                .count();
        if (secondaryHosts >= MAX_SECONDARY_HOSTS) {
            throw new IllegalStateException("Secondary host limit reached (max 5)");
        }

        members.add(FamilyGroupMember.create(hostUserId, FamilyMemberRole.SECONDARY_HOST));
    }

    public void rename(UserId requesterId, String newName) {
        ensurePrimaryHost(requesterId);
        this.name = validateName(newName);
    }

    public void removeMember(UserId requesterId, UserId memberUserId) {
        boolean isSelfRemoval = requesterId.equals(memberUserId);
        if (!isSelfRemoval) {
            ensurePrimaryHost(requesterId);
        }
        if (primaryHostUserId.equals(memberUserId)) {
            throw new IllegalStateException("Primary host cannot be removed from the family group");
        }

        boolean removed = members.removeIf(m -> m.getUserId().equals(memberUserId));
        if (!removed) {
            throw new IllegalStateException("Member not found in family group");
        }
    }

    public boolean hasMember(UserId userId) {
        return members.stream().anyMatch(m -> m.getUserId().equals(userId));
    }

    public boolean isPrimaryHost(UserId userId) {
        return primaryHostUserId.equals(userId);
    }

    public boolean isHost(UserId userId) {
        return members.stream().anyMatch(m -> m.getUserId().equals(userId) && m.isHost());
    }

    public List<UserId> getProtectedUserIds() {
        return members.stream()
                .filter(m -> m.getRole() == FamilyMemberRole.PROTECTED)
                .map(FamilyGroupMember::getUserId)
                .toList();
    }

    public List<UserId> getHostUserIds() {
        return members.stream()
                .filter(FamilyGroupMember::isHost)
                .map(FamilyGroupMember::getUserId)
                .toList();
    }

    private void ensurePrimaryHost(UserId requesterId) {
        if (!isPrimaryHost(requesterId)) {
            throw new IllegalStateException("Only primary host can perform this action");
        }
    }

    private void ensureMemberNotExists(UserId userId) {
        if (hasMember(userId)) {
            throw new IllegalStateException("User is already a member of this family group");
        }
    }

    private void ensureMemberLimitNotReached() {
        if (members.size() >= MAX_MEMBERS) {
            throw new IllegalStateException("Family group member limit reached (max " + MAX_MEMBERS + ")");
        }
    }

    private static String validateName(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Family group name is required");
        }
        String normalized = value.trim();
        if (normalized.length() < 3 || normalized.length() > 100) {
            throw new IllegalArgumentException("Family group name must be between 3 and 100 characters");
        }
        return normalized;
    }

    public FamilyGroupId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public UserId getPrimaryHostUserId() {
        return primaryHostUserId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<FamilyGroupMember> getMembers() {
        return Collections.unmodifiableList(members);
    }
}