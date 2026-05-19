package com.guardianapp.domain.exception;

/**
 * Domain exception for blacklist URL operations.
 */
public class BlacklistUrlException extends DomainException {

    private BlacklistUrlException(String message, String code) {
        super(message, code);
    }

    public static BlacklistUrlException invalidUrl(String url) {
        return new BlacklistUrlException(
            "Invalid blacklist URL: " + url,
            "BLACKLIST_URL_INVALID"
        );
    }

    public static BlacklistUrlException alreadyExists(String url) {
        return new BlacklistUrlException(
            "Blacklist URL already exists: " + url,
            "BLACKLIST_URL_ALREADY_EXISTS"
        );
    }
}
