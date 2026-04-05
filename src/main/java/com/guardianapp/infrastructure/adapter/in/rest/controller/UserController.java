package com.guardianapp.infrastructure.adapter.in.rest.controller;

import com.guardianapp.domain.model.User;
import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.domain.port.in.GetUserUseCase;
import com.guardianapp.domain.port.in.RegisterUserUseCase;
import com.guardianapp.domain.port.in.RegisterUserUseCase.RegisterUserCommand;
import com.guardianapp.infrastructure.adapter.in.rest.dto.request.RegisterUserRequest;
import com.guardianapp.infrastructure.adapter.in.rest.dto.response.UserResponse;
import com.guardianapp.infrastructure.adapter.in.rest.mapper.UserLinkRestMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for user operations.
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final RegisterUserUseCase registerUserUseCase;
    private final GetUserUseCase getUserUseCase;
    private final UserLinkRestMapper restMapper;

    public UserController(RegisterUserUseCase registerUserUseCase,
                          GetUserUseCase getUserUseCase,
                          UserLinkRestMapper restMapper) {
        this.registerUserUseCase = registerUserUseCase;
        this.getUserUseCase = getUserUseCase;
        this.restMapper = restMapper;
    }

    /**
     * Registers a new user.
     * 
     * POST /api/v1/users
     */
    @PostMapping
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody RegisterUserRequest request) {
        
        RegisterUserCommand command = new RegisterUserCommand(
            request.name(),
            request.email(),
            request.phone()
        );

        User user = registerUserUseCase.execute(command);
        UserResponse response = restMapper.toResponse(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Gets the current user's profile.
     * 
     * GET /api/v1/users/me
     * Header: X-User-Id
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile(
            @RequestHeader("X-User-Id") String userId) {
        
        User user = getUserUseCase.getCurrentUser(UserId.fromString(userId));
        UserResponse response = restMapper.toResponse(user);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Gets a user by ID.
     * 
     * GET /api/v1/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable String id) {
        return getUserUseCase.getById(UserId.fromString(id))
            .map(user -> ResponseEntity.ok(restMapper.toResponse(user)))
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Searches for a user by email.
     * 
     * GET /api/v1/users/search?email=xxx
     */
    @GetMapping("/search")
    public ResponseEntity<UserResponse> searchByEmail(
            @RequestParam String email) {
        return getUserUseCase.getByEmail(email)
            .map(user -> ResponseEntity.ok(restMapper.toResponse(user)))
            .orElse(ResponseEntity.notFound().build());
    }
}
