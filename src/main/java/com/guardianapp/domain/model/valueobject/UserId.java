package com.guardianapp.domain.model.valueobject;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object representing the unique identifier of a User.
 * Provides type-safety to avoid confusion between different ID types.
 */
public final class UserId {
    
    private final UUID value;

    private UserId(UUID value) {
        this.value = Objects.requireNonNull(value, "User ID cannot be null");
    }

    /**
     * Creates a new UserId with a random UUID.
     */
    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }

    /**
     * Creates a UserId from an existing UUID.
     */
    public static UserId of(UUID value) {
        return new UserId(value);
    }

    /**
     * Creates a UserId from a String representation of UUID.
     */
    public static UserId fromString(String value) {
        return new UserId(UUID.fromString(value));
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserId userId = (UserId) o;
        return Objects.equals(value, userId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
