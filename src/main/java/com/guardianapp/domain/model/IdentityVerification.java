package com.guardianapp.domain.model;

import com.guardianapp.domain.enums.VerificationStatus;
import com.guardianapp.domain.model.valueobject.LinkId;
import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.domain.model.valueobject.VerificationId;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain model for identity verification requests.
 * Created by protected user and resolved by host user.
 */
public class IdentityVerification {

    private static final int CODE_LENGTH = 6;
    private static final long DEFAULT_EXPIRATION_MINUTES = 5;

    private final VerificationId id;
    private final LinkId linkId;
    private final UserId protectedUserId;
    private final UserId hostUserId;
    private final String claimedPerson;
    private final String challengeCode;
    private VerificationStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime expiresAt;
    private LocalDateTime resolvedAt;
    private String resolutionNote;

    private IdentityVerification(
            VerificationId id,
            LinkId linkId,
            UserId protectedUserId,
            UserId hostUserId,
            String claimedPerson,
            String challengeCode,
            VerificationStatus status,
            LocalDateTime createdAt,
            LocalDateTime expiresAt,
            LocalDateTime resolvedAt,
            String resolutionNote) {
        this.id = Objects.requireNonNull(id, "Verification ID is required");
        this.linkId = Objects.requireNonNull(linkId, "Link ID is required");
        this.protectedUserId = Objects.requireNonNull(protectedUserId, "Protected user ID is required");
        this.hostUserId = Objects.requireNonNull(hostUserId, "Host user ID is required");
        this.claimedPerson = Objects.requireNonNull(claimedPerson, "Claimed person is required");
        this.challengeCode = Objects.requireNonNull(challengeCode, "Challenge code is required");
        this.status = Objects.requireNonNull(status, "Status is required");
        this.createdAt = Objects.requireNonNull(createdAt, "Created at is required");
        this.expiresAt = Objects.requireNonNull(expiresAt, "Expires at is required");
        this.resolvedAt = resolvedAt;
        this.resolutionNote = resolutionNote;
    }

    public static IdentityVerification create(
            LinkId linkId,
            UserId protectedUserId,
            UserId hostUserId,
            String claimedPerson) {
        if (claimedPerson == null || claimedPerson.isBlank()) {
            throw new IllegalArgumentException("Claimed person is required");
        }

        LocalDateTime now = LocalDateTime.now();
        return new IdentityVerification(
            VerificationId.generate(),
            linkId,
            protectedUserId,
            hostUserId,
            claimedPerson.trim(),
            generateChallengeCode(),
            VerificationStatus.PENDING,
            now,
            now.plusMinutes(DEFAULT_EXPIRATION_MINUTES),
            null,
            null
        );
    }

    public static IdentityVerification reconstitute(
            VerificationId id,
            LinkId linkId,
            UserId protectedUserId,
            UserId hostUserId,
            String claimedPerson,
            String challengeCode,
            VerificationStatus status,
            LocalDateTime createdAt,
            LocalDateTime expiresAt,
            LocalDateTime resolvedAt,
            String resolutionNote) {
        return new IdentityVerification(
            id,
            linkId,
            protectedUserId,
            hostUserId,
            claimedPerson,
            challengeCode,
            status,
            createdAt,
            expiresAt,
            resolvedAt,
            resolutionNote
        );
    }

    public void approve(UserId hostId, String note) {
        validateCanBeResolvedBy(hostId);
        this.status = VerificationStatus.APPROVED;
        this.resolvedAt = LocalDateTime.now();
        this.resolutionNote = note;
    }

    public void reject(UserId hostId, String note) {
        validateCanBeResolvedBy(hostId);
        this.status = VerificationStatus.REJECTED;
        this.resolvedAt = LocalDateTime.now();
        this.resolutionNote = note;
    }

    public void expireIfNeeded() {
        if (status == VerificationStatus.PENDING && isExpired()) {
            this.status = VerificationStatus.EXPIRED;
            this.resolvedAt = LocalDateTime.now();
        }
    }

    public boolean isPending() {
        return status == VerificationStatus.PENDING;
    }

    public boolean isResolved() {
        return status.isFinalState();
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public long getRemainingSeconds() {
        if (isExpired()) {
            return 0;
        }
        return Duration.between(LocalDateTime.now(), expiresAt).toSeconds();
    }

    private void validateCanBeResolvedBy(UserId hostId) {
        Objects.requireNonNull(hostId, "Host ID is required");
        if (!this.hostUserId.equals(hostId)) {
            throw new IllegalStateException("Only the host can resolve this verification");
        }
        if (status != VerificationStatus.PENDING) {
            throw new IllegalStateException("Verification is not pending");
        }
        if (isExpired()) {
            this.status = VerificationStatus.EXPIRED;
            this.resolvedAt = LocalDateTime.now();
            throw new IllegalStateException("Verification has expired");
        }
    }

    private static String generateChallengeCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    public VerificationId getId() {
        return id;
    }

    public LinkId getLinkId() {
        return linkId;
    }

    public UserId getProtectedUserId() {
        return protectedUserId;
    }

    public UserId getHostUserId() {
        return hostUserId;
    }

    public String getClaimedPerson() {
        return claimedPerson;
    }

    public String getChallengeCode() {
        return challengeCode;
    }

    public VerificationStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public String getResolutionNote() {
        return resolutionNote;
    }
}
