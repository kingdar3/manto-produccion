package com.guardianapp.domain.exception;

/**
 * Domain exception for invitation-related errors.
 */
public class InvitationException extends DomainException {

    private InvitationException(String message, String code) {
        super(message, code);
    }

    public static InvitationException notFound(String token) {
        return new InvitationException(
            "Invitation not found with token: " + token,
            "INVITATION_NOT_FOUND"
        );
    }

    public static InvitationException notFoundById(String id) {
        return new InvitationException(
            "Invitation not found with ID: " + id,
            "INVITATION_NOT_FOUND"
        );
    }

    public static InvitationException expired(String token) {
        return new InvitationException(
            "Invitation has expired: " + token,
            "INVITATION_EXPIRED"
        );
    }

    public static InvitationException alreadyAccepted(String token) {
        return new InvitationException(
            "Invitation has already been accepted: " + token,
            "INVITATION_ALREADY_ACCEPTED"
        );
    }

    public static InvitationException cancelled(String token) {
        return new InvitationException(
            "Invitation has been cancelled: " + token,
            "INVITATION_CANCELLED"
        );
    }

    public static InvitationException invalidStatus(String currentStatus, String expectedStatus) {
        return new InvitationException(
            "Invalid invitation status. Current: " + currentStatus + ", Expected: " + expectedStatus,
            "INVITATION_INVALID_STATUS"
        );
    }

    public static InvitationException cannotInviteSelf() {
        return new InvitationException(
            "Cannot create an invitation for yourself",
            "INVITATION_SELF_NOT_ALLOWED"
        );
    }

    public static InvitationException hostNotFound(String hostId) {
        return new InvitationException(
            "Host user not found: " + hostId,
            "INVITATION_HOST_NOT_FOUND"
        );
    }
}
