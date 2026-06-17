package com.guardianapp.domain.exception;

/**
 * Domain exception for family group operations.
 */
public class FamilyGroupException extends DomainException {

    private FamilyGroupException(String message, String code) {
        super(message, code);
    }

    public static FamilyGroupException notFound(String groupId) {
        return new FamilyGroupException(
                "Family group not found with ID: " + groupId,
                "FAMILY_GROUP_NOT_FOUND"
        );
    }

    public static FamilyGroupException userNotFound(String userId) {
        return new FamilyGroupException(
                "User not found with ID: " + userId,
                "FAMILY_GROUP_USER_NOT_FOUND"
        );
    }

    public static FamilyGroupException memberAlreadyExists(String userId) {
        return new FamilyGroupException(
                "User already belongs to family group: " + userId,
                "FAMILY_GROUP_MEMBER_EXISTS"
        );
    }

    public static FamilyGroupException memberLimitReached() {
        return new FamilyGroupException(
                "Family group member limit reached (max 4)",
                "FAMILY_GROUP_MEMBER_LIMIT_REACHED"
        );
    }

    public static FamilyGroupException secondaryHostLimitReached() {
        return new FamilyGroupException(
                "Secondary host limit reached (max 5)",
                "FAMILY_GROUP_SECONDARY_HOST_LIMIT"
        );
    }

    public static FamilyGroupException notAuthorized(String userId) {
        return new FamilyGroupException(
                "User " + userId + " is not authorized for this family group operation",
                "FAMILY_GROUP_NOT_AUTHORIZED"
        );
    }

    public static FamilyGroupException memberNotFound(String userId) {
        return new FamilyGroupException(
                "Member not found in family group: " + userId,
                "FAMILY_GROUP_MEMBER_NOT_FOUND"
        );
    }
}