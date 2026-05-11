package com.guardianapp.domain.exception;

/**
 * Domain exception for family invitation operations.
 */
public class FamilyInvitationException extends DomainException {

    private FamilyInvitationException(String message, String code) {
        super(message, code);
    }

    public static FamilyInvitationException notFound(String tokenOrId) {
        return new FamilyInvitationException(
                "Family invitation not found: " + tokenOrId,
                "FAMILY_INVITATION_NOT_FOUND"
        );
    }

    public static FamilyInvitationException expired(String token) {
        return new FamilyInvitationException(
                "Family invitation has expired: " + token,
                "FAMILY_INVITATION_EXPIRED"
        );
    }

    public static FamilyInvitationException cancelled(String token) {
        return new FamilyInvitationException(
                "Family invitation is cancelled: " + token,
                "FAMILY_INVITATION_CANCELLED"
        );
    }

    public static FamilyInvitationException alreadyAccepted(String token) {
        return new FamilyInvitationException(
                "Family invitation already accepted: " + token,
                "FAMILY_INVITATION_ALREADY_ACCEPTED"
        );
    }

    public static FamilyInvitationException notAuthorized(String userId) {
        return new FamilyInvitationException(
                "User " + userId + " is not authorized for this family invitation action",
                "FAMILY_INVITATION_NOT_AUTHORIZED"
        );
    }
}
