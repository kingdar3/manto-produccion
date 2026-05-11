package com.guardianapp.domain.exception;

/**
 * Domain exception for emergency alert related errors.
 */
public class EmergencyAlertException extends DomainException {

    private EmergencyAlertException(String message, String code) {
        super(message, code);
    }

    public static EmergencyAlertException notFound(String alertId) {
        return new EmergencyAlertException(
                "Emergency alert not found with ID: " + alertId,
                "EMERGENCY_ALERT_NOT_FOUND"
        );
    }

    public static EmergencyAlertException linkNotFound(String linkId) {
        return new EmergencyAlertException(
                "Link not found for emergency alert creation: " + linkId,
                "EMERGENCY_ALERT_LINK_NOT_FOUND"
        );
    }

    public static EmergencyAlertException linkNotActive(String linkId) {
        return new EmergencyAlertException(
                "Link is not active: " + linkId,
                "EMERGENCY_ALERT_LINK_NOT_ACTIVE"
        );
    }

    public static EmergencyAlertException protectedUserNotInLink(String userId, String linkId) {
        return new EmergencyAlertException(
                "User " + userId + " is not the protected user of link " + linkId,
                "EMERGENCY_ALERT_PROTECTED_NOT_IN_LINK"
        );
    }

    public static EmergencyAlertException notAuthorizedToResolve(String userId) {
        return new EmergencyAlertException(
                "User " + userId + " is not authorized to resolve this emergency alert",
                "EMERGENCY_ALERT_NOT_AUTHORIZED"
        );
    }

    public static EmergencyAlertException alreadyResolved(String alertId) {
        return new EmergencyAlertException(
                "Emergency alert has already been resolved: " + alertId,
                "EMERGENCY_ALERT_ALREADY_RESOLVED"
        );
    }
}
