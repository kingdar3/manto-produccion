package com.guardianapp.application.service;

import com.guardianapp.domain.exception.UserException;
import com.guardianapp.domain.model.User;
import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.domain.port.in.GetUserUseCase;
import com.guardianapp.domain.port.in.RegisterUserUseCase;
import com.guardianapp.domain.port.out.UserRepositoryPort;

import java.util.Optional;

/**
 * Application service that implements user use cases.
 * Orchestrates business logic and coordinates with output ports.
 */
public class UserService implements RegisterUserUseCase, GetUserUseCase {

    private final UserRepositoryPort userRepository;

    public UserService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    // ==================== RegisterUserUseCase ====================

    @Override
    public User execute(RegisterUserCommand command) {
        // Check that email is not already registered
        if (userRepository.existsByEmail(command.email())) {
            throw UserException.emailAlreadyExists(command.email());
        }

        // Create domain user with business validations
        // Note: No role - users can be HOST and PROTECTED in different links
        User user = User.create(
            command.name(),
            command.email(),
            command.phone()
        );

        // Persist and return
        return userRepository.save(user);
    }

    // ==================== GetUserUseCase ====================

    @Override
    public Optional<User> getById(UserId userId) {
        return userRepository.findById(userId);
    }

    @Override
    public Optional<User> getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User getCurrentUser(UserId userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> UserException.notFound(userId.toString()));
    }
}
