package com.guardianapp.domain.model;

import com.guardianapp.domain.enums.EmergencyAlertStatus;
import com.guardianapp.domain.enums.EmergencyResolutionType;
import com.guardianapp.domain.model.valueobject.EmergencyAlertId;
import com.guardianapp.domain.model.valueobject.LinkId;
import com.guardianapp.domain.model.valueobject.UserId;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain model for emergency alerts triggered by protected users.
 */
public class EmergencyAlert {

    private static final int MAX_DURATION_MINUTES = 30;

    private final EmergencyAlertId id;
    private final LinkId linkId;
    private final UserId protectedUserId;
    private final UserId primaryHostUserId;
    private final double latitude;
    private final double longitude;
    private EmergencyAlertStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    private UserId resolvedByUserId;
    private EmergencyResolutionType resolutionType;
    private String resolutionNote;

    private EmergencyAlert(
            EmergencyAlertId id,
            LinkId linkId,
            UserId protectedUserId,
            UserId primaryHostUserId,
            double latitude,
            double longitude,
            EmergencyAlertStatus status,
            LocalDateTime createdAt,
            LocalDateTime resolvedAt,
            UserId resolvedByUserId,
            EmergencyResolutionType resolutionType,
            String resolutionNote) {
        this.id = Objects.requireNonNull(id, "Emergency alert ID is required");
        this.linkId = Objects.requireNonNull(linkId, "Link ID is required");
        this.protectedUserId = Objects.requireNonNull(protectedUserId, "Protected user ID is required");
        this.primaryHostUserId = Objects.requireNonNull(primaryHostUserId, "Primary host user ID is required");
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = Objects.requireNonNull(status, "Status is required");
        this.createdAt = Objects.requireNonNull(createdAt, "Created at is required");
        this.resolvedAt = resolvedAt;
        this.resolvedByUserId = resolvedByUserId;
        this.resolutionType = resolutionType;
        this.resolutionNote = resolutionNote;
    }

    public static EmergencyAlert create(
            LinkId linkId,
            UserId protectedUserId,
            UserId primaryHostUserId,
            double latitude,
            double longitude) {
        validateCoordinates(latitude, longitude);

        return new EmergencyAlert(
                EmergencyAlertId.generate(),
                linkId,
                protectedUserId,
                primaryHostUserId,
                latitude,
                longitude,
                EmergencyAlertStatus.ACTIVE,
                LocalDateTime.now(),
                null,
                null,
                null,
                null
        );
    }

    public static EmergencyAlert reconstitute(
            EmergencyAlertId id,
            LinkId linkId,
            UserId protectedUserId,
            UserId primaryHostUserId,
            double latitude,
            double longitude,
            EmergencyAlertStatus status,
            LocalDateTime createdAt,
            LocalDateTime resolvedAt,
            UserId resolvedByUserId,
            EmergencyResolutionType resolutionType,
            String resolutionNote) {
        return new EmergencyAlert(
                id,
                linkId,
                protectedUserId,
                primaryHostUserId,
                latitude,
                longitude,
                status,
                createdAt,
                resolvedAt,
                resolvedByUserId,
                resolutionType,
                resolutionNote
        );
    }

    public void resolve(UserId hostUserId, EmergencyResolutionType resolutionType, String note) {
        Objects.requireNonNull(hostUserId, "Host user ID is required");
        Objects.requireNonNull(resolutionType, "Resolution type is required");
        validateResolvableBy(hostUserId);

        this.status = EmergencyAlertStatus.RESOLVED;
        this.resolutionType = resolutionType;
        this.resolutionNote = note;
        this.resolvedAt = LocalDateTime.now();
        this.resolvedByUserId = hostUserId;
    }

    public boolean isActive() {
        return status == EmergencyAlertStatus.ACTIVE;
    }

    public boolean isResolved() {
        return status.isResolved();
    }

    public long getSecondsSinceCreation() {
        return Duration.between(createdAt, LocalDateTime.now()).toSeconds();
    }

    public boolean isOverMaxDuration() {
        return Duration.between(createdAt, LocalDateTime.now()).toMinutes() >= MAX_DURATION_MINUTES;
    }

    private static void validateCoordinates(double latitude, double longitude) {
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90");
        }
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180");
        }
    }

    private void validateResolvableBy(UserId hostUserId) {
        if (isResolved()) {
            throw new IllegalStateException("Emergency alert is already resolved");
        }
        if (!this.primaryHostUserId.equals(hostUserId)) {
            throw new IllegalStateException("Only the primary host can resolve this emergency alert");
        }
    }

    public EmergencyAlertId getId() {
        return id;
    }

    public LinkId getLinkId() {
        return linkId;
    }

    public UserId getProtectedUserId() {
        return protectedUserId;
    }

    public UserId getPrimaryHostUserId() {
        return primaryHostUserId;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public EmergencyAlertStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public UserId getResolvedByUserId() {
        return resolvedByUserId;
    }

    public EmergencyResolutionType getResolutionType() {
        return resolutionType;
    }

    public String getResolutionNote() {
        return resolutionNote;
    }
}
