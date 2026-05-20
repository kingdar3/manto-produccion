package com.guardianapp.domain.model;

import com.guardianapp.domain.enums.LinkStatus;
import com.guardianapp.domain.exception.LinkException;
import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.domain.model.valueobject.LinkId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain entity representing the link between a Host and a Protected user.
 * This class is pure Java, without external framework dependencies.
 */
public class Link {

    private final LinkId id;
    private final UserId hostId;
    private final UserId protectedId;
    private LinkStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime updatedAt;

    /**
     * Private constructor - use factory methods.
     */
    private Link(LinkId id, UserId hostId, UserId protectedId,
                 LinkStatus status, LocalDateTime createdAt,
                 LocalDateTime confirmedAt, LocalDateTime updatedAt) {
        this.id = id;
        this.hostId = hostId;
        this.protectedId = protectedId;
        this.status = status;
        this.createdAt = createdAt;
        this.confirmedAt = confirmedAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Creates a new link between host and protected user.
     * With the 8-character invitation flow, links are activated immediately.
     */
    public static Link createRequest(UserId hostId, UserId protectedId) {
        Objects.requireNonNull(hostId, "Host ID cannot be null");
        Objects.requireNonNull(protectedId, "Protected ID cannot be null");

        if (hostId.equals(protectedId)) {
            throw new LinkException("A user cannot link with themselves");
        }

        LocalDateTime now = LocalDateTime.now();
        return new Link(
            LinkId.generate(),
            hostId,
            protectedId,
            LinkStatus.ACTIVE,
            now,
            now,
            now
        );
    }

    /**
     * Reconstructs a link from the persistence layer.
     */
    public static Link reconstruct(LinkId id, UserId hostId, UserId protectedId,
                                    LinkStatus status, LocalDateTime createdAt,
                                    LocalDateTime confirmedAt, LocalDateTime updatedAt) {
        return new Link(id, hostId, protectedId, status, createdAt, confirmedAt, updatedAt);
    }

    /**
     * Rejects the link request.
     */
    public void reject() {
        if (status != LinkStatus.PENDING) {
            throw new LinkException(
                "This operation is only allowed for pending links. Current status: " + status
            );
        }
        this.status = LinkStatus.REJECTED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Cancels an active or pending link.
     */
    public void cancel() {
        if (status.isTerminalState()) {
            throw new LinkException("Cannot cancel a link in status: " + status);
        }
        this.status = LinkStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Checks if the link is active and allows monitoring.
     */
    public boolean isActive() {
        return this.status == LinkStatus.ACTIVE;
    }


    /**
     * Checks if a specific user is part of this link.
     */
    public boolean involvesUser(UserId userId) {
        return hostId.equals(userId) || protectedId.equals(userId);
    }

    /**
     * Checks if a user is the host of this link.
     */
    public boolean isHost(UserId userId) {
        return hostId.equals(userId);
    }

    /**
     * Checks if a user is the protected of this link.
     */
    public boolean isProtected(UserId userId) {
        return protectedId.equals(userId);
    }

    // Getters
    public LinkId getId() {
        return id;
    }

    public UserId getHostId() {
        return hostId;
    }

    public UserId getProtectedId() {
        return protectedId;
    }

    public LinkStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Link link = (Link) o;
        return Objects.equals(id, link.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Link{" +
                "id=" + id +
                ", hostId=" + hostId +
                ", protectedId=" + protectedId +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}
