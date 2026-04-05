package com.guardianapp.domain.port.in;

import com.guardianapp.domain.model.User;

/**
 * Input port (Use Case) for registering new users.
 * Defines the contract that the application layer must implement.
 */
public interface RegisterUserUseCase {

    /**
     * Registers a new user in the system.
     *
     * @param command Data required for registration
     * @return The registered user
     */
    User execute(RegisterUserCommand command);

    /**
     * Command that encapsulates input data for user registration.
     * Note: No role field - users can be both HOST and PROTECTED in different links.
     */
    record RegisterUserCommand(
        String name,
        String email,
        String phone
    ) {
        public RegisterUserCommand {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Name is required");
            }
            if (email == null || email.isBlank()) {
                throw new IllegalArgumentException("Email is required");
            }
            if (phone == null || phone.isBlank()) {
                throw new IllegalArgumentException("Phone is required");
            }
        }
    }
}
