package com.guardianapp.domain.model.valueobject;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing an identity verification ID.
 */
public final class VerificationId {

    private final UUID value;

    private VerificationId(UUID value) {
        this.value = Objects.requireNonNull(value, "Verification ID cannot be null");
    }

    public static VerificationId generate() {
        return new VerificationId(UUID.randomUUID());
    }

    public static VerificationId of(UUID value) {
        return new VerificationId(value);
    }

    public static VerificationId fromString(String value) {
        Objects.requireNonNull(value, "Verification ID cannot be null");
        return new VerificationId(UUID.fromString(value));
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VerificationId that = (VerificationId) o;
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
