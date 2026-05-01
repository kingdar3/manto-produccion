package com.guardianapp.domain.model.valueobject;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing a family group ID.
 */
public final class FamilyGroupId {

    private final UUID value;

    private FamilyGroupId(UUID value) {
        this.value = Objects.requireNonNull(value, "FamilyGroupId cannot be null");
    }

    public static FamilyGroupId generate() {
        return new FamilyGroupId(UUID.randomUUID());
    }

    public static FamilyGroupId of(UUID value) {
        return new FamilyGroupId(value);
    }

    public static FamilyGroupId fromString(String value) {
        Objects.requireNonNull(value, "FamilyGroupId string cannot be null");
        return new FamilyGroupId(UUID.fromString(value));
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FamilyGroupId that = (FamilyGroupId) o;
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
