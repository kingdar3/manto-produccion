package com.guardianapp.domain.model.valueobject;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object representing the unique identifier of a Link.
 * Provides type-safety to avoid confusion between different ID types.
 */
public final class LinkId {
    
    private final UUID value;

    private LinkId(UUID value) {
        this.value = Objects.requireNonNull(value, "Link ID cannot be null");
    }

    /**
     * Creates a new LinkId with a random UUID.
     */
    public static LinkId generate() {
        return new LinkId(UUID.randomUUID());
    }

    /**
     * Creates a LinkId from an existing UUID.
     */
    public static LinkId of(UUID value) {
        return new LinkId(value);
    }

    /**
     * Creates a LinkId from a String representation of UUID.
     */
    public static LinkId fromString(String value) {
        return new LinkId(UUID.fromString(value));
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinkId linkId = (LinkId) o;
        return Objects.equals(value, linkId.value);
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
