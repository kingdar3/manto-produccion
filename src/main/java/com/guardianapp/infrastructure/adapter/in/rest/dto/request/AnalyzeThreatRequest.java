package com.guardianapp.infrastructure.adapter.in.rest.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Request DTO for Google Safe Browsing analysis.
 */
public record AnalyzeThreatRequest(
    @Size(max = 5000, message = "Message must not exceed 5000 characters")
    String message,

    @NotEmpty(message = "At least one URL is required")
    @Size(max = 20, message = "A maximum of 20 URLs is allowed per request")
    List<@Size(max = 2048, message = "URL must not exceed 2048 characters") String> urls,

    @Size(max = 100, message = "Sender must not exceed 100 characters")
    String sender
) {}
