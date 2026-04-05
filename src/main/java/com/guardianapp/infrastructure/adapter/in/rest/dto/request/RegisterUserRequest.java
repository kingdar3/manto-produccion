package com.guardianapp.infrastructure.adapter.in.rest.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for user registration requests.
 * Note: No role field - users can be both HOST and PROTECTED in different links.
 */
public record RegisterUserRequest(
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    String name,

    @NotBlank(message = "Email is required")
    @Email(message = "Email format is not valid")
    String email,

    @NotBlank(message = "Phone is required")
    @Size(min = 9, max = 20, message = "Phone must be between 9 and 20 characters")
    String phone
) {}
