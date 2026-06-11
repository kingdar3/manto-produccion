package com.guardianapp.domain.exception;

/**
 * Domain exception for SMS threat alert errors.
 */
public class SmsThreatAlertException extends DomainException {

    private SmsThreatAlertException(String message, String code) {
        super(message, code);
    }

    public static SmsThreatAlertException notFound(String alertId) {
        return new SmsThreatAlertException(
            "SMS threat alert not found with ID: " + alertId,
            "SMS_THREAT_ALERT_NOT_FOUND"
        );
    }

    public static SmsThreatAlertException alreadyResolved(String alertId) {
        return new SmsThreatAlertException(
            "SMS threat alert has already been resolved: " + alertId,
            "SMS_THREAT_ALERT_ALREADY_RESOLVED"
        );
    }

    public static SmsThreatAlertException linkNotFound(String linkId) {
        return new SmsThreatAlertException(
            "Link not found for SMS threat alert creation: " + linkId,
            "SMS_THREAT_ALERT_LINK_NOT_FOUND"
        );
    }

    public static SmsThreatAlertException linkNotActive(String linkId) {
        return new SmsThreatAlertException(
            "Link is not active: " + linkId,
            "SMS_THREAT_ALERT_LINK_NOT_ACTIVE"
        );
    }

    public static SmsThreatAlertException userNotInLink(String userId, String linkId) {
        return new SmsThreatAlertException(
            "User " + userId + " is not part of link " + linkId,
            "SMS_THREAT_ALERT_USER_NOT_IN_LINK"
        );
    }

    public static SmsThreatAlertException notAuthorizedToResolve(String userId) {
        return new SmsThreatAlertException(
            "User " + userId + " is not authorized to resolve this SMS threat alert",
            "SMS_THREAT_ALERT_NOT_AUTHORIZED"
        );
    }
}
