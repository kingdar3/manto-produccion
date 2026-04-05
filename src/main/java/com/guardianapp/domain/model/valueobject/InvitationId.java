package com.guardianapp.domain.model.valueobject;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object representing an Invitation ID.
 * Immutable and validated.
 */
public final class InvitationId {

    private final UUID value;

    private InvitationId(UUID value) {
        this.value = Objects.requireNonNull(value, "InvitationId cannot be null");
    }

    /**
     * Creates a new random InvitationId.
     */
    public static InvitationId generate() {
        return new InvitationId(UUID.randomUUID());
    }

    /**
     * Creates an InvitationId from a UUID.
     */
    public static InvitationId of(UUID value) {
        return new InvitationId(value);
    }

    /**
     * Creates an InvitationId from a String.
     */
    public static InvitationId fromString(String value) {
        Objects.requireNonNull(value, "InvitationId string cannot be null");
        return new InvitationId(UUID.fromString(value));
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InvitationId that = (InvitationId) o;
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
