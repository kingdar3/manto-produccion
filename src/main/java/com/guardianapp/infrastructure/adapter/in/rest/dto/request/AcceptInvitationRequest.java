package com.guardianapp.infrastructure.adapter.in.rest.dto.request;

/**
 * DTO for accepting an invitation request.
 * The protected user sends their token to accept.
 */
public record AcceptInvitationRequest(
    // No body needed - token comes from path, userId from header
) {}
