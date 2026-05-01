package com.guardianapp.domain.model;

import com.guardianapp.domain.enums.FamilyInvitationStatus;
import com.guardianapp.domain.enums.FamilyMemberRole;
import com.guardianapp.domain.model.valueobject.FamilyGroupId;
import com.guardianapp.domain.model.valueobject.FamilyInvitationId;
import com.guardianapp.domain.model.valueobject.UserId;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain model for family invitations.
 */
public class FamilyInvitation {

    private static final int TOKEN_LENGTH = 8;
    private static final int EXPIRATION_HOURS = 48;
    private static final String TOKEN_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    private final FamilyInvitationId id;
    private final FamilyGroupId familyGroupId;
    private final UserId invitedByUserId;
    private final FamilyMemberRole targetRole;
    private final String token;
    private FamilyInvitationStatus status;
    private final LocalDateTime expiresAt;
    private final LocalDateTime createdAt;
    private LocalDateTime acceptedAt;
    private UserId acceptedByUserId;

    private FamilyInvitation(
            FamilyInvitationId id,
            FamilyGroupId familyGroupId,
            UserId invitedByUserId,
            FamilyMemberRole targetRole,
            String token,
            FamilyInvitationStatus status,
            LocalDateTime expiresAt,
            LocalDateTime createdAt,
            LocalDateTime acceptedAt,
            UserId acceptedByUserId) {
        this.id = Objects.requireNonNull(id, "Family invitation ID is required");
        this.familyGroupId = Objects.requireNonNull(familyGroupId, "Family group ID is required");
        this.invitedByUserId = Objects.requireNonNull(invitedByUserId, "Inviter user ID is required");
        this.targetRole = Objects.requireNonNull(targetRole, "Target role is required");
        this.token = Objects.requireNonNull(token, "Token is required");
        this.status = Objects.requireNonNull(status, "Status is required");
        this.expiresAt = Objects.requireNonNull(expiresAt, "Expires at is required");
        this.createdAt = Objects.requireNonNull(createdAt, "Created at is required");
        this.acceptedAt = acceptedAt;
        this.acceptedByUserId = acceptedByUserId;
    }

    public static FamilyInvitation create(
            FamilyGroupId familyGroupId,
            UserId invitedByUserId,
            FamilyMemberRole targetRole) {
        if (targetRole == FamilyMemberRole.PRIMARY_HOST) {
            throw new IllegalArgumentException("Cannot invite another primary host");
        }

        return new FamilyInvitation(
                FamilyInvitationId.generate(),
                familyGroupId,
                invitedByUserId,
                targetRole,
                generateToken(),
                FamilyInvitationStatus.PENDING,
                LocalDateTime.now().plusHours(EXPIRATION_HOURS),
                LocalDateTime.now(),
                null,
                null
        );
    }

    public static FamilyInvitation reconstitute(
            FamilyInvitationId id,
            FamilyGroupId familyGroupId,
            UserId invitedByUserId,
            FamilyMemberRole targetRole,
            String token,
            FamilyInvitationStatus status,
            LocalDateTime expiresAt,
            LocalDateTime createdAt,
            LocalDateTime acceptedAt,
            UserId acceptedByUserId) {
        return new FamilyInvitation(
                id,
                familyGroupId,
                invitedByUserId,
                targetRole,
                token,
                status,
                expiresAt,
                createdAt,
                acceptedAt,
                acceptedByUserId
        );
    }

    public void accept(UserId userId) {
        validateCanBeAccepted();
        this.status = FamilyInvitationStatus.ACCEPTED;
        this.acceptedAt = LocalDateTime.now();
        this.acceptedByUserId = userId;
    }

    public void cancel(UserId requesterId) {
        if (!this.invitedByUserId.equals(requesterId)) {
            throw new IllegalStateException("Only inviter can cancel this family invitation");
        }
        if (this.status != FamilyInvitationStatus.PENDING) {
            throw new IllegalStateException("Only pending family invitations can be cancelled");
        }
        this.status = FamilyInvitationStatus.CANCELLED;
    }

    public void markExpired() {
        if (this.status == FamilyInvitationStatus.PENDING) {
            this.status = FamilyInvitationStatus.EXPIRED;
        }
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isPending() {
        return this.status == FamilyInvitationStatus.PENDING;
    }

    private void validateCanBeAccepted() {
        if (this.status != FamilyInvitationStatus.PENDING) {
            throw new IllegalStateException("Family invitation is not pending");
        }
        if (isExpired()) {
            this.status = FamilyInvitationStatus.EXPIRED;
            throw new IllegalStateException("Family invitation has expired");
        }
    }

    private static String generateToken() {
        SecureRandom random = new SecureRandom();
        StringBuilder token = new StringBuilder(TOKEN_LENGTH);
        for (int i = 0; i < TOKEN_LENGTH; i++) {
            token.append(TOKEN_CHARS.charAt(random.nextInt(TOKEN_CHARS.length())));
        }
        return token.toString();
    }

    public FamilyInvitationId getId() {
        return id;
    }

    public FamilyGroupId getFamilyGroupId() {
        return familyGroupId;
    }

    public UserId getInvitedByUserId() {
        return invitedByUserId;
    }

    public FamilyMemberRole getTargetRole() {
        return targetRole;
    }

    public String getToken() {
        return token;
    }

    public FamilyInvitationStatus getStatus() {
        return status;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getAcceptedAt() {
        return acceptedAt;
    }

    public UserId getAcceptedByUserId() {
        return acceptedByUserId;
    }
}
