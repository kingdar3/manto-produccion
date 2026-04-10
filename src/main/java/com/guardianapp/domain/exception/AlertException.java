package com.guardianapp.domain.exception;

/**
 * Domain exception for alert-related errors.
 */
public class AlertException extends DomainException {

    private AlertException(String message, String code) {
        super(message, code);
    }

    public static AlertException notFound(String alertId) {
        return new AlertException(
            "Alert not found with ID: " + alertId,
            "ALERT_NOT_FOUND"
        );
    }

    public static AlertException alreadyResolved(String alertId) {
        return new AlertException(
            "Alert has already been resolved: " + alertId,
            "ALERT_ALREADY_RESOLVED"
        );
    }

    public static AlertException linkNotFound(String linkId) {
        return new AlertException(
            "Link not found for alert creation: " + linkId,
            "ALERT_LINK_NOT_FOUND"
        );
    }

    public static AlertException linkNotActive(String linkId) {
        return new AlertException(
            "Link is not active: " + linkId,
            "ALERT_LINK_NOT_ACTIVE"
        );
    }

    public static AlertException userNotInLink(String userId, String linkId) {
        return new AlertException(
            "User " + userId + " is not part of link " + linkId,
            "ALERT_USER_NOT_IN_LINK"
        );
    }

    public static AlertException notAuthorizedToResolve(String userId) {
        return new AlertException(
            "User " + userId + " is not authorized to resolve this alert",
            "ALERT_NOT_AUTHORIZED"
        );
    }

    public static AlertException invalidUrl(String url) {
        return new AlertException(
            "Invalid URL format: " + url,
            "ALERT_INVALID_URL"
        );
    }

    public static AlertException invalidResolution(String resolution) {
        return new AlertException(
            "Invalid resolution type: " + resolution + ". Must be SAFE or BLOCKED",
            "ALERT_INVALID_RESOLUTION"
        );
    }
}
