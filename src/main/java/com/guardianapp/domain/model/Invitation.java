package com.guardianapp.domain.model;

import com.guardianapp.domain.enums.InvitationStatus;
import com.guardianapp.domain.model.valueobject.InvitationId;
import com.guardianapp.domain.model.valueobject.UserId;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain model representing an invitation to create a link.
 * The host creates an invitation and shares the token with the protected user.
 * Pure Java - no framework annotations.
 */
public class Invitation {

    private static final int TOKEN_LENGTH = 8;
    private static final int EXPIRATION_HOURS = 48;
    private static final String TOKEN_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // Excludes confusing chars (0,O,1,I)

    private final InvitationId id;
    private final UserId hostId;
    private final String token;
    private final String hostName; // Name to show in the invitation
    private InvitationStatus status;
    private final LocalDateTime expiresAt;
    private final LocalDateTime createdAt;
    private LocalDateTime acceptedAt;
    private UserId acceptedByUserId; // The protected user who accepted

    private Invitation(InvitationId id, UserId hostId, String token, String hostName,
                       InvitationStatus status, LocalDateTime expiresAt, 
                       LocalDateTime createdAt, LocalDateTime acceptedAt,
                       UserId acceptedByUserId) {
        this.id = Objects.requireNonNull(id, "Invitation ID is required");
        this.hostId = Objects.requireNonNull(hostId, "Host ID is required");
        this.token = Objects.requireNonNull(token, "Token is required");
        this.hostName = hostName;
        this.status = Objects.requireNonNull(status, "Status is required");
        this.expiresAt = Objects.requireNonNull(expiresAt, "Expiration date is required");
        this.createdAt = Objects.requireNonNull(createdAt, "Creation date is required");
        this.acceptedAt = acceptedAt;
        this.acceptedByUserId = acceptedByUserId;
    }

    /**
     * Creates a new invitation.
     * Generates a unique token and sets expiration to 48 hours.
     */
    public static Invitation create(UserId hostId, String hostName) {
        return new Invitation(
            InvitationId.generate(),
            hostId,
            generateToken(),
            hostName,
            InvitationStatus.PENDING,
            LocalDateTime.now().plusHours(EXPIRATION_HOURS),
            LocalDateTime.now(),
            null,
            null
        );
    }

    /**
     * Reconstitutes an invitation from persistence.
     */
    public static Invitation reconstitute(InvitationId id, UserId hostId, String token,
                                          String hostName, InvitationStatus status,
                                          LocalDateTime expiresAt, LocalDateTime createdAt,
                                          LocalDateTime acceptedAt, UserId acceptedByUserId) {
        return new Invitation(id, hostId, token, hostName, status, expiresAt, 
                              createdAt, acceptedAt, acceptedByUserId);
    }

    /**
     * Accepts the invitation. Called when a protected user accepts.
     */
    public void accept(UserId protectedUserId) {
        validateCanBeAccepted();
        this.status = InvitationStatus.ACCEPTED;
        this.acceptedAt = LocalDateTime.now();
        this.acceptedByUserId = protectedUserId;
    }

    /**
     * Cancels the invitation. Only the host can cancel.
     */
    public void cancel(UserId userId) {
        if (!this.hostId.equals(userId)) {
            throw new IllegalStateException("Only the host can cancel the invitation");
        }
        if (this.status != InvitationStatus.PENDING) {
            throw new IllegalStateException("Can only cancel pending invitations");
        }
        this.status = InvitationStatus.CANCELLED;
    }

    /**
     * Marks the invitation as expired.
     */
    public void markAsExpired() {
        if (this.status == InvitationStatus.PENDING) {
            this.status = InvitationStatus.EXPIRED;
        }
    }

    /**
     * Checks if the invitation is valid (pending and not expired).
     */
    public boolean isValid() {
        return this.status == InvitationStatus.PENDING && 
               LocalDateTime.now().isBefore(this.expiresAt);
    }

    /**
     * Checks if the invitation has expired.
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }

    /**
     * Gets remaining minutes until expiration.
     */
    public long getRemainingMinutes() {
        if (isExpired()) {
            return 0;
        }
        return java.time.Duration.between(LocalDateTime.now(), expiresAt).toMinutes();
    }

    /**
     * Generates the shareable link for this invitation.
     */
    public String getShareableLink() {
        return "manto://invite/" + this.token;
    }

    private void validateCanBeAccepted() {
        if (this.status != InvitationStatus.PENDING) {
            throw new IllegalStateException("Invitation is not pending");
        }
        if (isExpired()) {
            this.status = InvitationStatus.EXPIRED;
            throw new IllegalStateException("Invitation has expired");
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

    // Getters
    public InvitationId getId() { return id; }
    public UserId getHostId() { return hostId; }
    public String getToken() { return token; }
    public String getHostName() { return hostName; }
    public InvitationStatus getStatus() { return status; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getAcceptedAt() { return acceptedAt; }
    public UserId getAcceptedByUserId() { return acceptedByUserId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Invitation that = (Invitation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Invitation{" +
                "id=" + id +
                ", hostId=" + hostId +
                ", token='" + token + '\'' +
                ", status=" + status +
                ", expiresAt=" + expiresAt +
                '}';
    }
}
