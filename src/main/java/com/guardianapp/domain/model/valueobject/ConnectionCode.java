package com.guardianapp.domain.model.valueobject;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Value Object representing the connection code (PIN) for linking devices.
 * The code is a 6-digit numeric PIN with expiration time.
 */
public final class ConnectionCode {
    
    private static final int CODE_LENGTH = 6;
    private static final int EXPIRATION_MINUTES = 15;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final String code;
    private final LocalDateTime createdAt;
    private final LocalDateTime expiresAt;

    private ConnectionCode(String code, LocalDateTime createdAt, LocalDateTime expiresAt) {
        this.code = code;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    /**
     * Generates a new connection code with random values.
     */
    public static ConnectionCode generate() {
        String code = generateRandomCode();
        LocalDateTime now = LocalDateTime.now();
        return new ConnectionCode(code, now, now.plusMinutes(EXPIRATION_MINUTES));
    }

    /**
     * Creates a connection code from existing values (for reconstruction from DB).
     */
    public static ConnectionCode of(String code, LocalDateTime createdAt, LocalDateTime expiresAt) {
        validateFormat(code);
        return new ConnectionCode(code, createdAt, expiresAt);
    }

    /**
     * Validates if the provided code matches this connection code.
     */
    public boolean validate(String inputCode) {
        if (inputCode == null || inputCode.isBlank()) {
            return false;
        }
        return this.code.equals(inputCode.trim()) && !isExpired();
    }

    /**
     * Checks if the code has expired.
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * Calculates the remaining minutes before expiration.
     */
    public long remainingMinutes() {
        if (isExpired()) {
            return 0;
        }
        return java.time.Duration.between(LocalDateTime.now(), expiresAt).toMinutes();
    }

    private static String generateRandomCode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }

    private static void validateFormat(String code) {
        if (code == null || code.length() != CODE_LENGTH) {
            throw new IllegalArgumentException(
                "Code must have exactly " + CODE_LENGTH + " digits"
            );
        }
        if (!code.matches("\\d+")) {
            throw new IllegalArgumentException("Code must contain only digits");
        }
    }

    public String getCode() {
        return code;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConnectionCode that = (ConnectionCode) o;
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        // For security, don't show the complete code in logs
        return "ConnectionCode{code=***" + code.substring(code.length() - 2) + 
               ", expiresAt=" + expiresAt + "}";
    }
}
