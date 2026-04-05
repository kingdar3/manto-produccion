package com.guardianapp.domain.exception;

/**
 * Exception for errors related to link operations.
 */
public class LinkException extends DomainException {

    public LinkException(String message) {
        super(message, "LINK_ERROR");
    }

    public LinkException(String message, Throwable cause) {
        super(message, "LINK_ERROR", cause);
    }

    /**
     * Creates an exception for when a link is not found.
     */
    public static LinkException notFound(String linkId) {
        return new LinkException("Link not found with ID: " + linkId);
    }

    /**
     * Creates an exception for when a link already exists between two users.
     */
    public static LinkException alreadyExists() {
        return new LinkException("An active or pending link already exists between these users");
    }

    /**
     * Creates an exception for invalid connection code.
     */
    public static LinkException invalidCode() {
        return new LinkException("Connection code is invalid or has expired");
    }

    /**
     * Creates an exception for operation not allowed.
     */
    public static LinkException operationNotAllowed(String reason) {
        return new LinkException("Operation not allowed: " + reason);
    }
}
