package com.guardianapp.domain.model;

import com.guardianapp.domain.enums.SmsThreatAlertStatus;
import com.guardianapp.domain.enums.UrlThreatStatus;
import com.guardianapp.domain.model.valueobject.LinkId;
import com.guardianapp.domain.model.valueobject.SmsThreatAlertId;
import com.guardianapp.domain.model.valueobject.UserId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain model for SMS threat alerts raised by protected users.
 */
public class SmsThreatAlert {

    private final SmsThreatAlertId id;
    private final LinkId linkId;
    private final UserId protectedUserId;
    private final UserId hostUserId;
    private final String sender;
    private final String messageExcerpt;
    private final String detectedUrl;
    private final UrlThreatStatus analysisStatus;
    private final String analysisReason;
    private SmsThreatAlertStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    private UserId resolvedByUserId;
    private String resolutionNote;

    private SmsThreatAlert(SmsThreatAlertId id,
                           LinkId linkId,
                           UserId protectedUserId,
                           UserId hostUserId,
                           String sender,
                           String messageExcerpt,
                           String detectedUrl,
                           UrlThreatStatus analysisStatus,
                           String analysisReason,
                           SmsThreatAlertStatus status,
                           LocalDateTime createdAt,
                           LocalDateTime resolvedAt,
                           UserId resolvedByUserId,
                           String resolutionNote) {
        this.id = Objects.requireNonNull(id, "SMS threat alert ID is required");
        this.linkId = Objects.requireNonNull(linkId, "Link ID is required");
        this.protectedUserId = Objects.requireNonNull(protectedUserId, "Protected user ID is required");
        this.hostUserId = Objects.requireNonNull(hostUserId, "Host user ID is required");
        this.sender = Objects.requireNonNull(sender, "Sender is required");
        this.messageExcerpt = Objects.requireNonNull(messageExcerpt, "Message excerpt is required");
        this.detectedUrl = Objects.requireNonNull(detectedUrl, "Detected URL is required");
        this.analysisStatus = Objects.requireNonNull(analysisStatus, "Analysis status is required");
        this.analysisReason = analysisReason;
        this.status = Objects.requireNonNull(status, "Status is required");
        this.createdAt = Objects.requireNonNull(createdAt, "Creation date is required");
        this.resolvedAt = resolvedAt;
        this.resolvedByUserId = resolvedByUserId;
        this.resolutionNote = resolutionNote;
    }

    public static SmsThreatAlert create(LinkId linkId,
                                        UserId protectedUserId,
                                        UserId hostUserId,
                                        String sender,
                                        String messageExcerpt,
                                        String detectedUrl,
                                        UrlThreatStatus analysisStatus,
                                        String analysisReason) {
        if (sender == null || sender.isBlank()) {
            throw new IllegalArgumentException("Sender cannot be blank");
        }
        if (messageExcerpt == null || messageExcerpt.isBlank()) {
            throw new IllegalArgumentException("Message excerpt cannot be blank");
        }
        if (detectedUrl == null || detectedUrl.isBlank()) {
            throw new IllegalArgumentException("Detected URL cannot be blank");
        }

        return new SmsThreatAlert(
            SmsThreatAlertId.generate(),
            linkId,
            protectedUserId,
            hostUserId,
            sender,
            messageExcerpt,
            detectedUrl,
            analysisStatus,
            analysisReason,
            SmsThreatAlertStatus.PENDING,
            LocalDateTime.now(),
            null,
            null,
            null
        );
    }

    public static SmsThreatAlert reconstitute(SmsThreatAlertId id,
                                              LinkId linkId,
                                              UserId protectedUserId,
                                              UserId hostUserId,
                                              String sender,
                                              String messageExcerpt,
                                              String detectedUrl,
                                              UrlThreatStatus analysisStatus,
                                              String analysisReason,
                                              SmsThreatAlertStatus status,
                                              LocalDateTime createdAt,
                                              LocalDateTime resolvedAt,
                                              UserId resolvedByUserId,
                                              String resolutionNote) {
        return new SmsThreatAlert(
            id,
            linkId,
            protectedUserId,
            hostUserId,
            sender,
            messageExcerpt,
            detectedUrl,
            analysisStatus,
            analysisReason,
            status,
            createdAt,
            resolvedAt,
            resolvedByUserId,
            resolutionNote
        );
    }

    public void resolveAsSafe(UserId hostId, String note) {
        validateCanBeResolved();
        this.status = SmsThreatAlertStatus.RESOLVED_SAFE;
        this.resolvedAt = LocalDateTime.now();
        this.resolvedByUserId = hostId;
        this.resolutionNote = note;
    }

    public void resolveAsBlocked(UserId hostId, String note) {
        validateCanBeResolved();
        this.status = SmsThreatAlertStatus.RESOLVED_BLOCKED;
        this.resolvedAt = LocalDateTime.now();
        this.resolvedByUserId = hostId;
        this.resolutionNote = note;
    }

    public boolean isPending() {
        return this.status == SmsThreatAlertStatus.PENDING;
    }

    public boolean isResolved() {
        return this.status.isResolved();
    }

    public boolean isUrlAllowed() {
        return this.status.isUrlAllowed();
    }

    public long getMinutesSinceCreation() {
        return java.time.Duration.between(createdAt, LocalDateTime.now()).toMinutes();
    }

    private void validateCanBeResolved() {
        if (this.status != SmsThreatAlertStatus.PENDING) {
            throw new IllegalStateException("SMS threat alert has already been resolved");
        }
    }

    public SmsThreatAlertId getId() { return id; }
    public LinkId getLinkId() { return linkId; }
    public UserId getProtectedUserId() { return protectedUserId; }
    public UserId getHostUserId() { return hostUserId; }
    public String getSender() { return sender; }
    public String getMessageExcerpt() { return messageExcerpt; }
    public String getDetectedUrl() { return detectedUrl; }
    public UrlThreatStatus getAnalysisStatus() { return analysisStatus; }
    public String getAnalysisReason() { return analysisReason; }
    public SmsThreatAlertStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public UserId getResolvedByUserId() { return resolvedByUserId; }
    public String getResolutionNote() { return resolutionNote; }
}
