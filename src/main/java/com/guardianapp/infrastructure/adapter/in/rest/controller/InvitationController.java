package com.guardianapp.infrastructure.adapter.in.rest.controller;

import com.guardianapp.domain.exception.InvitationException;
import com.guardianapp.domain.model.Invitation;
import com.guardianapp.domain.model.Link;
import com.guardianapp.domain.model.valueobject.InvitationId;
import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.domain.port.in.InvitationUseCase;
import com.guardianapp.domain.port.in.InvitationUseCase.AcceptInvitationCommand;
import com.guardianapp.domain.port.in.InvitationUseCase.CreateInvitationCommand;
import com.guardianapp.infrastructure.adapter.in.rest.dto.response.InvitationResponse;
import com.guardianapp.infrastructure.adapter.in.rest.dto.response.LinkResponse;
import com.guardianapp.infrastructure.adapter.in.rest.mapper.UserLinkRestMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for invitation operations.
 * Handles the invitation flow for linking users.
 */
@RestController
@RequestMapping("/api/v1/invitations")
public class InvitationController {

    private final InvitationUseCase invitationUseCase;
    private final UserLinkRestMapper restMapper;

    public InvitationController(InvitationUseCase invitationUseCase,
                                 UserLinkRestMapper restMapper) {
        this.invitationUseCase = invitationUseCase;
        this.restMapper = restMapper;
    }

    /**
     * Creates a new invitation.
     * The host creates an invitation and receives a shareable link/token.
     * 
     * POST /api/v1/invitations
     * Header: X-User-Id (Host ID)
     */
    @PostMapping
    public ResponseEntity<InvitationResponse> create(
            @RequestHeader("X-User-Id") String hostId) {

        CreateInvitationCommand command = new CreateInvitationCommand(
            UserId.fromString(hostId)
        );

        Invitation invitation = invitationUseCase.create(command);
        InvitationResponse response = restMapper.toResponse(invitation);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Gets an invitation by its token.
     * Used when a protected user opens the shared link.
     * 
     * GET /api/v1/invitations/{token}
     */
    @GetMapping("/{token}")
    public ResponseEntity<InvitationResponse> getByToken(@PathVariable String token) {
        return invitationUseCase.getByToken(token)
            .map(invitation -> ResponseEntity.ok(restMapper.toResponse(invitation)))
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Gets all invitations created by the current user.
     * 
     * GET /api/v1/invitations/mine
     * Header: X-User-Id (Host ID)
     */
    @GetMapping("/mine")
    public ResponseEntity<List<InvitationResponse>> getMyInvitations(
            @RequestHeader("X-User-Id") String hostId) {
        
        List<Invitation> invitations = invitationUseCase.getByHost(UserId.fromString(hostId));
        List<InvitationResponse> response = restMapper.toInvitationResponseList(invitations);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Gets pending invitations created by the current user.
     * 
     * GET /api/v1/invitations/mine/pending
     * Header: X-User-Id (Host ID)
     */
    @GetMapping("/mine/pending")
    public ResponseEntity<List<InvitationResponse>> getMyPendingInvitations(
            @RequestHeader("X-User-Id") String hostId) {
        
        List<Invitation> invitations = invitationUseCase.getPendingByHost(UserId.fromString(hostId));
        List<InvitationResponse> response = restMapper.toInvitationResponseList(invitations);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Accepts an invitation.
     * The protected user accepts the invitation, creating a PENDING link.
     * Then the host must share the PIN code for final confirmation.
     * 
     * POST /api/v1/invitations/{token}/accept
     * Header: X-User-Id (Protected user ID)
     */
    @PostMapping("/{token}/accept")
    public ResponseEntity<LinkResponse> accept(
            @PathVariable String token,
            @RequestHeader("X-User-Id") String protectedUserId) {

        AcceptInvitationCommand command = new AcceptInvitationCommand(
            token,
            UserId.fromString(protectedUserId)
        );

        Link link = invitationUseCase.accept(command);
        LinkResponse response = restMapper.toResponse(link);

        return ResponseEntity.ok(response);
    }

    /**
     * Cancels an invitation.
     * Only the host can cancel their own invitations.
     * 
     * POST /api/v1/invitations/{id}/cancel
     * Header: X-User-Id (Host ID)
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<InvitationResponse> cancel(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String hostId) {

        Invitation invitation = invitationUseCase.cancel(
            InvitationId.fromString(id),
            UserId.fromString(hostId)
        );

        InvitationResponse response = restMapper.toResponse(invitation);
        return ResponseEntity.ok(response);
    }
}
