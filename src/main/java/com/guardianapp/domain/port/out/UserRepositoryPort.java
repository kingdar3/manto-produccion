package com.guardianapp.domain.port.out;

import com.guardianapp.domain.model.User;
import com.guardianapp.domain.model.valueobject.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Output port for user persistence.
 * Defines the contract that persistence adapters must implement.
 */
public interface UserRepositoryPort {

    /**
     * Saves a user (create or update).
     *
     * @param user User to save
     * @return Saved user
     */
    User save(User user);

    /**
     * Finds a user by ID.
     *
     * @param id User ID
     * @return Optional with user if exists
     */
    Optional<User> findById(UserId id);

    /**
     * Finds a user by email.
     *
     * @param email User email
     * @return Optional with user if exists
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user exists with the given email.
     *
     * @param email Email to check
     * @return true if exists
     */
    boolean existsByEmail(String email);

    /**
     * Finds all active users.
     *
     * @return List of active users
     */
    List<User> findAllActive();

    /**
     * Deletes a user by ID.
     *
     * @param id User ID to delete
     */
    void delete(UserId id);
}
