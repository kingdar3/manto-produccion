package com.guardianapp.domain.port.out;

import com.guardianapp.domain.enums.AlertStatus;
import com.guardianapp.domain.model.Alert;
import com.guardianapp.domain.model.valueobject.AlertId;
import com.guardianapp.domain.model.valueobject.LinkId;
import com.guardianapp.domain.model.valueobject.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Output port for alert persistence.
 * Defines the contract that persistence adapters must implement.
 */
public interface AlertRepositoryPort {

    /**
     * Saves an alert (create or update).
     *
     * @param alert Alert to save
     * @return Saved alert
     */
    Alert save(Alert alert);

    /**
     * Finds an alert by ID.
     *
     * @param id Alert ID
     * @return Optional with alert if exists
     */
    Optional<Alert> findById(AlertId id);

    /**
     * Finds all alerts for a specific link.
     *
     * @param linkId Link ID
     * @return List of alerts for this link
     */
    List<Alert> findByLinkId(LinkId linkId);

    /**
     * Finds alerts by link and status.
     *
     * @param linkId Link ID
     * @param status Alert status
     * @return List of alerts
     */
    List<Alert> findByLinkIdAndStatus(LinkId linkId, AlertStatus status);

    /**
     * Finds all pending alerts for links where the given user is host.
     * Used for host polling.
     *
     * @param hostId Host user ID
     * @return List of pending alerts
     */
    List<Alert> findPendingByHostId(UserId hostId);

    /**
     * Finds all alerts for a protected user.
     *
     * @param protectedUserId Protected user ID
     * @return List of alerts
     */
    List<Alert> findByProtectedUserId(UserId protectedUserId);

    /**
     * Finds pending alerts for a protected user.
     *
     * @param protectedUserId Protected user ID
     * @return List of pending alerts
     */
    List<Alert> findPendingByProtectedUserId(UserId protectedUserId);

    /**
     * Counts pending alerts for a host user.
     * Used for badge/notification count.
     *
     * @param hostId Host user ID
     * @return Count of pending alerts
     */
    long countPendingByHostId(UserId hostId);

    /**
     * Deletes an alert by ID.
     *
     * @param id Alert ID to delete
     */
    void delete(AlertId id);

    /**
     * Checks if a pending alert exists for the same URL in a link.
     * Prevents duplicate alerts for the same URL.
     *
     * @param linkId Link ID
     * @param url Suspicious URL
     * @return true if pending alert exists
     */
    boolean existsPendingByLinkIdAndUrl(LinkId linkId, String url);
}
