package com.guardianapp.domain.model.valueobject;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing a family invitation ID.
 */
public final class FamilyInvitationId {

    private final UUID value;

    private FamilyInvitationId(UUID value) {
        this.value = Objects.requireNonNull(value, "FamilyInvitationId cannot be null");
    }

    public static FamilyInvitationId generate() {
        return new FamilyInvitationId(UUID.randomUUID());
    }

    public static FamilyInvitationId of(UUID value) {
        return new FamilyInvitationId(value);
    }

    public static FamilyInvitationId fromString(String value) {
        Objects.requireNonNull(value, "FamilyInvitationId string cannot be null");
        return new FamilyInvitationId(UUID.fromString(value));
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FamilyInvitationId that = (FamilyInvitationId) o;
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
