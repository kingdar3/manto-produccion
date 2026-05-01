package com.guardianapp.domain.model.valueobject;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing an emergency alert ID.
 */
public final class EmergencyAlertId {

    private final UUID value;

    private EmergencyAlertId(UUID value) {
        this.value = Objects.requireNonNull(value, "EmergencyAlertId cannot be null");
    }

    public static EmergencyAlertId generate() {
        return new EmergencyAlertId(UUID.randomUUID());
    }

    public static EmergencyAlertId of(UUID value) {
        return new EmergencyAlertId(value);
    }

    public static EmergencyAlertId fromString(String value) {
        Objects.requireNonNull(value, "EmergencyAlertId string cannot be null");
        return new EmergencyAlertId(UUID.fromString(value));
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmergencyAlertId that = (EmergencyAlertId) o;
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
