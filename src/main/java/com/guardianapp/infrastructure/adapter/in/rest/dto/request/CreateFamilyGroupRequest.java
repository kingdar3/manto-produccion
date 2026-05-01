package com.guardianapp.infrastructure.adapter.in.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating a family group.
 */
public record CreateFamilyGroupRequest(
        @NotBlank(message = "Family group name is required")
        @Size(min = 3, max = 100, message = "Family group name must be between 3 and 100 characters")
        String name
) {
}
