package com.guardianapp.domain.port.in;

import com.guardianapp.domain.model.User;
import com.guardianapp.domain.model.valueobject.UserId;

import java.util.Optional;

/**
 * Input port (Use Case) for querying users.
 * Defines the contract for user query operations.
 */
public interface GetUserUseCase {

    /**
     * Gets a user by their ID.
     *
     * @param userId User ID
     * @return Optional with user if exists
     */
    Optional<User> getById(UserId userId);

    /**
     * Gets a user by their email.
     *
     * @param email User email
     * @return Optional with user if exists
     */
    Optional<User> getByEmail(String email);

    /**
     * Gets the current authenticated user.
     * In the future, this will use the security context.
     * For now, it receives the userId as parameter.
     *
     * @param userId User ID (from auth header)
     * @return The user
     * @throws com.guardianapp.domain.exception.UserException if not found
     */
    User getCurrentUser(UserId userId);
}
