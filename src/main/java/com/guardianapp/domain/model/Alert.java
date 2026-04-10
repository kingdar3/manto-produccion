package com.guardianapp.domain.model;

import com.guardianapp.domain.enums.AlertStatus;
import com.guardianapp.domain.model.valueobject.AlertId;
import com.guardianapp.domain.model.valueobject.LinkId;
import com.guardianapp.domain.model.valueobject.UserId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain model representing an alert triggered when the protected user
 * encounters a suspicious URL. The host must resolve the alert.
 * Pure Java - no framework annotations.
 */
public class Alert {

    private final AlertId id;
    private final LinkId linkId;
    private final UserId protectedUserId;
    private final String suspiciousUrl;
    private final String reason;
    private AlertStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    private UserId resolvedByUserId;
    private String resolutionNote;

    private Alert(AlertId id, LinkId linkId, UserId protectedUserId,
                  String suspiciousUrl, String reason, AlertStatus status,
                  LocalDateTime createdAt, LocalDateTime resolvedAt,
                  UserId resolvedByUserId, String resolutionNote) {
        this.id = Objects.requireNonNull(id, "Alert ID is required");
        this.linkId = Objects.requireNonNull(linkId, "Link ID is required");
        this.protectedUserId = Objects.requireNonNull(protectedUserId, "Protected user ID is required");
        this.suspiciousUrl = Objects.requireNonNull(suspiciousUrl, "Suspicious URL is required");
        this.reason = reason;
        this.status = Objects.requireNonNull(status, "Status is required");
        this.createdAt = Objects.requireNonNull(createdAt, "Creation date is required");
        this.resolvedAt = resolvedAt;
        this.resolvedByUserId = resolvedByUserId;
        this.resolutionNote = resolutionNote;
    }

    /**
     * Creates a new alert when a suspicious URL is detected.
     *
     * @param linkId The link between host and protected user
     * @param protectedUserId The protected user who encountered the URL
     * @param suspiciousUrl The URL that was flagged
     * @param reason Why the URL was flagged (e.g., "Phishing detected", "Unknown domain")
     * @return A new pending alert
     */
    public static Alert create(LinkId linkId, UserId protectedUserId, 
                                String suspiciousUrl, String reason) {
        Objects.requireNonNull(suspiciousUrl, "Suspicious URL is required");
        if (suspiciousUrl.isBlank()) {
            throw new IllegalArgumentException("Suspicious URL cannot be blank");
        }

        return new Alert(
            AlertId.generate(),
            linkId,
            protectedUserId,
            suspiciousUrl,
            reason != null ? reason : "Potentially dangerous URL detected",
            AlertStatus.PENDING,
            LocalDateTime.now(),
            null,
            null,
            null
        );
    }

    /**
     * Reconstitutes an alert from persistence.
     */
    public static Alert reconstitute(AlertId id, LinkId linkId, UserId protectedUserId,
                                      String suspiciousUrl, String reason, AlertStatus status,
                                      LocalDateTime createdAt, LocalDateTime resolvedAt,
                                      UserId resolvedByUserId, String resolutionNote) {
        return new Alert(id, linkId, protectedUserId, suspiciousUrl, reason, status,
                         createdAt, resolvedAt, resolvedByUserId, resolutionNote);
    }

    /**
     * Resolves the alert as SAFE - allows the protected user to access the URL.
     *
     * @param hostId The host user resolving the alert
     * @param note Optional note explaining the decision
     */
    public void resolveAsSafe(UserId hostId, String note) {
        validateCanBeResolved();
        this.status = AlertStatus.RESOLVED_SAFE;
        this.resolvedAt = LocalDateTime.now();
        this.resolvedByUserId = hostId;
        this.resolutionNote = note;
    }

    /**
     * Resolves the alert as BLOCKED - keeps the URL blocked.
     *
     * @param hostId The host user resolving the alert
     * @param note Optional note explaining the decision
     */
    public void resolveAsBlocked(UserId hostId, String note) {
        validateCanBeResolved();
        this.status = AlertStatus.RESOLVED_BLOCKED;
        this.resolvedAt = LocalDateTime.now();
        this.resolvedByUserId = hostId;
        this.resolutionNote = note;
    }

    /**
     * Checks if the alert is still pending resolution.
     */
    public boolean isPending() {
        return this.status == AlertStatus.PENDING;
    }

    /**
     * Checks if the alert has been resolved (in any way).
     */
    public boolean isResolved() {
        return this.status.isResolved();
    }

    /**
     * Checks if the URL was marked as safe.
     */
    public boolean isUrlAllowed() {
        return this.status.isUrlAllowed();
    }

    /**
     * Gets the time elapsed since the alert was created, in minutes.
     */
    public long getMinutesSinceCreation() {
        return java.time.Duration.between(createdAt, LocalDateTime.now()).toMinutes();
    }

    private void validateCanBeResolved() {
        if (this.status != AlertStatus.PENDING) {
            throw new IllegalStateException("Alert has already been resolved");
        }
    }

    // Getters
    public AlertId getId() { return id; }
    public LinkId getLinkId() { return linkId; }
    public UserId getProtectedUserId() { return protectedUserId; }
    public String getSuspiciousUrl() { return suspiciousUrl; }
    public String getReason() { return reason; }
    public AlertStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public UserId getResolvedByUserId() { return resolvedByUserId; }
    public String getResolutionNote() { return resolutionNote; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Alert alert = (Alert) o;
        return Objects.equals(id, alert.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Alert{" +
                "id=" + id +
                ", linkId=" + linkId +
                ", protectedUserId=" + protectedUserId +
                ", suspiciousUrl='" + suspiciousUrl + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}
