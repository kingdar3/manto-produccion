package com.guardianapp.domain.port.in;

import com.guardianapp.domain.model.Alert;
import com.guardianapp.domain.model.valueobject.AlertId;
import com.guardianapp.domain.model.valueobject.LinkId;
import com.guardianapp.domain.model.valueobject.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Input port (Use Case) for managing alerts.
 * Defines the contract for alert operations in the phishing detection flow.
 */
public interface AlertUseCase {

    /**
     * Creates a new alert when a suspicious URL is detected.
     * Called by the protected user's SecureBrowser.
     *
     * @param command Data to create the alert
     * @return The created alert with PENDING status
     */
    Alert create(CreateAlertCommand command);

    /**
     * Gets an alert by its ID.
     * Used by protected to poll for resolution status.
     *
     * @param alertId The alert ID
     * @return Optional with alert if found
     */
    Optional<Alert> getById(AlertId alertId);

    /**
     * Gets all pending alerts for a host user.
     * Used by host dashboard for polling.
     *
     * @param hostId The host user ID
     * @return List of pending alerts for links where user is host
     */
    List<Alert> getPendingForHost(UserId hostId);

    /**
     * Gets all alerts for a specific link.
     *
     * @param linkId The link ID
     * @return List of alerts for this link
     */
    List<Alert> getByLink(LinkId linkId);

    /**
     * Resolves an alert.
     * The host decides whether to allow or block the URL.
     *
     * @param command Data to resolve the alert
     * @return The resolved alert
     */
    Alert resolve(ResolveAlertCommand command);

    /**
     * Command to create an alert.
     */
    record CreateAlertCommand(
        LinkId linkId,
        UserId protectedUserId,
        String suspiciousUrl,
        String reason
    ) {
        public CreateAlertCommand {
            if (linkId == null) {
                throw new IllegalArgumentException("Link ID is required");
            }
            if (protectedUserId == null) {
                throw new IllegalArgumentException("Protected user ID is required");
            }
            if (suspiciousUrl == null || suspiciousUrl.isBlank()) {
                throw new IllegalArgumentException("Suspicious URL is required");
            }
        }
    }

    /**
     * Command to resolve an alert.
     */
    record ResolveAlertCommand(
        AlertId alertId,
        UserId hostId,
        boolean allowAccess,
        String note
    ) {
        public ResolveAlertCommand {
            if (alertId == null) {
                throw new IllegalArgumentException("Alert ID is required");
            }
            if (hostId == null) {
                throw new IllegalArgumentException("Host ID is required");
            }
        }

        /**
         * Creates a command to mark URL as safe.
         */
        public static ResolveAlertCommand markAsSafe(AlertId alertId, UserId hostId, String note) {
            return new ResolveAlertCommand(alertId, hostId, true, note);
        }

        /**
         * Creates a command to keep URL blocked.
         */
        public static ResolveAlertCommand keepBlocked(AlertId alertId, UserId hostId, String note) {
            return new ResolveAlertCommand(alertId, hostId, false, note);
        }
    }
}
