package com.guardianapp.domain.model.valueobject;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing an SMS threat alert ID.
 */
public final class SmsThreatAlertId {

    private final UUID value;

    private SmsThreatAlertId(UUID value) {
        this.value = Objects.requireNonNull(value, "SmsThreatAlertId cannot be null");
    }

    public static SmsThreatAlertId generate() {
        return new SmsThreatAlertId(UUID.randomUUID());
    }

    public static SmsThreatAlertId of(UUID value) {
        return new SmsThreatAlertId(value);
    }

    public static SmsThreatAlertId fromString(String value) {
        Objects.requireNonNull(value, "SmsThreatAlertId string cannot be null");
        return new SmsThreatAlertId(UUID.fromString(value));
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SmsThreatAlertId that = (SmsThreatAlertId) o;
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
