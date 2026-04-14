package com.guardianapp.domain.exception;

/**
 * Domain exception for identity verification related errors.
 */
public class IdentityVerificationException extends DomainException {

    private IdentityVerificationException(String message, String code) {
        super(message, code);
    }

    public static IdentityVerificationException notFound(String id) {
        return new IdentityVerificationException(
            "Identity verification not found with ID: " + id,
            "VERIFICATION_NOT_FOUND"
        );
    }

    public static IdentityVerificationException alreadyResolved(String id) {
        return new IdentityVerificationException(
            "Identity verification already resolved: " + id,
            "VERIFICATION_ALREADY_RESOLVED"
        );
    }

    public static IdentityVerificationException expired(String id) {
        return new IdentityVerificationException(
            "Identity verification has expired: " + id,
            "VERIFICATION_EXPIRED"
        );
    }

    public static IdentityVerificationException linkNotFound(String linkId) {
        return new IdentityVerificationException(
            "Link not found for identity verification: " + linkId,
            "VERIFICATION_LINK_NOT_FOUND"
        );
    }

    public static IdentityVerificationException linkNotActive(String linkId) {
        return new IdentityVerificationException(
            "Link is not active: " + linkId,
            "VERIFICATION_LINK_NOT_ACTIVE"
        );
    }

    public static IdentityVerificationException userNotAuthorized(String userId) {
        return new IdentityVerificationException(
            "User is not authorized for this operation: " + userId,
            "VERIFICATION_NOT_AUTHORIZED"
        );
    }
}
