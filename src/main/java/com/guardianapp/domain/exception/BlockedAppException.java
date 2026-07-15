package com.guardianapp.domain.exception;

/**
 * Domain exception for blocked app operations.
 */
public class BlockedAppException extends DomainException {

    private BlockedAppException(String message, String code) {
        super(message, code);
    }

    public static BlockedAppException alreadyBlocked(String packageName) {
        return new BlockedAppException(
                "App is already blocked: " + packageName,
                "BLOCKED_APP_ALREADY_EXISTS"
        );
    }

    public static BlockedAppException notFound(String blockedAppId) {
        return new BlockedAppException(
                "Blocked app not found with ID: " + blockedAppId,
                "BLOCKED_APP_NOT_FOUND"
        );
    }

    public static BlockedAppException notAuthorized(String userId) {
        return new BlockedAppException(
                "User " + userId + " is not authorized for this blocked app operation",
                "BLOCKED_APP_NOT_AUTHORIZED"
        );
    }
}
