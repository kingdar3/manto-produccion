package com.guardianapp.domain.exception;

/**
 * Exception for errors related to user operations.
 */
public class UserException extends DomainException {

    public UserException(String message) {
        super(message, "USER_ERROR");
    }

    public UserException(String message, Throwable cause) {
        super(message, "USER_ERROR", cause);
    }

    /**
     * Creates an exception for when a user is not found.
     */
    public static UserException notFound(String userId) {
        return new UserException("User not found with ID: " + userId);
    }

    /**
     * Creates an exception for duplicate email.
     */
    public static UserException emailAlreadyExists(String email) {
        return new UserException("A user already exists with email: " + email);
    }

    /**
     * Creates an exception for inactive user.
     */
    public static UserException userInactive(String userName) {
        return new UserException("User '" + userName + "' is inactive and cannot participate in links");
    }
}
