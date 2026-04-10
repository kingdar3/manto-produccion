package com.guardianapp.domain.model.valueobject;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object representing an Alert ID.
 * Immutable and validated.
 */
public final class AlertId {

    private final UUID value;

    private AlertId(UUID value) {
        this.value = Objects.requireNonNull(value, "AlertId cannot be null");
    }

    /**
     * Creates a new random AlertId.
     */
    public static AlertId generate() {
        return new AlertId(UUID.randomUUID());
    }

    /**
     * Creates an AlertId from a UUID.
     */
    public static AlertId of(UUID value) {
        return new AlertId(value);
    }

    /**
     * Creates an AlertId from a String.
     */
    public static AlertId fromString(String value) {
        Objects.requireNonNull(value, "AlertId string cannot be null");
        return new AlertId(UUID.fromString(value));
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlertId that = (AlertId) o;
        return Objects.equals(value, that.value);
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
