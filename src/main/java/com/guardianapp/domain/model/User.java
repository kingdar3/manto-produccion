package com.guardianapp.domain.model;

import com.guardianapp.domain.model.valueobject.UserId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain entity representing a user of the Guardian application.
 * This class is pure Java, without external framework dependencies.
 * 
 * Note: Users don't have a fixed role. The role (HOST/PROTECTED) is determined
 * by each Link relationship. A user can be HOST in one link and PROTECTED in another.
 */
public class User {

    private final UserId id;
    private String name;
    private String email;
    private String phone;
    private final LocalDateTime createdAt;
    private boolean active;

    /**
     * Private constructor - use factory methods.
     */
    private User(UserId id, String name, String email, String phone, 
                 LocalDateTime createdAt, boolean active) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.createdAt = createdAt;
        this.active = active;
    }

    /**
     * Creates a new user with business validations.
     */
    public static User create(String name, String email, String phone) {
        validateName(name);
        validateEmail(email);
        validatePhone(phone);

        return new User(
            UserId.generate(),
            name.trim(),
            email.toLowerCase().trim(),
            phone.trim(),
            LocalDateTime.now(),
            true
        );
    }

    /**
     * Reconstructs a user from the persistence layer.
     */
    public static User reconstruct(UserId id, String name, String email, 
                                    String phone, LocalDateTime createdAt, 
                                    boolean active) {
        return new User(id, name, email, phone, createdAt, active);
    }

    /**
     * Updates the user profile.
     */
    public void updateProfile(String name, String phone) {
        validateName(name);
        validatePhone(phone);
        this.name = name.trim();
        this.phone = phone.trim();
    }

    /**
     * Deactivates the user account.
     */
    public void deactivate() {
        this.active = false;
    }

    /**
     * Reactivates the user account.
     */
    public void activate() {
        this.active = true;
    }

    /**
     * Checks if the user can participate in links (must be active).
     */
    public boolean canParticipateInLinks() {
        return this.active;
    }

    // Business validations
    private static void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (name.trim().length() < 2) {
            throw new IllegalArgumentException("Name must have at least 2 characters");
        }
        if (name.trim().length() > 100) {
            throw new IllegalArgumentException("Name cannot exceed 100 characters");
        }
    }

    private static void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("Email format is not valid");
        }
    }

    private static void validatePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone cannot be empty");
        }
        String cleanPhone = phone.replaceAll("[\\s-]", "");
        if (!cleanPhone.matches("^\\+?\\d{9,15}$")) {
            throw new IllegalArgumentException("Phone format is not valid");
        }
    }

    // Getters
    public UserId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", active=" + active +
                '}';
    }
}
