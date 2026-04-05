package com.guardianapp.domain.port.in;

import com.guardianapp.domain.model.Invitation;
import com.guardianapp.domain.model.Link;
import com.guardianapp.domain.model.valueobject.InvitationId;
import com.guardianapp.domain.model.valueobject.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Input port (Use Case) for managing invitations.
 * Defines the contract for invitation operations.
 */
public interface InvitationUseCase {

    /**
     * Creates a new invitation.
     * The host creates an invitation to share with a potential protected user.
     *
     * @param command Data to create the invitation
     * @return The created invitation with token and shareable link
     */
    Invitation create(CreateInvitationCommand command);

    /**
     * Gets an invitation by its token.
     * Used when a protected user opens the shared link.
     *
     * @param token The invitation token
     * @return Optional with invitation if found and valid
     */
    Optional<Invitation> getByToken(String token);

    /**
     * Gets an invitation by its ID.
     *
     * @param invitationId The invitation ID
     * @return Optional with invitation if found
     */
    Optional<Invitation> getById(InvitationId invitationId);

    /**
     * Accepts an invitation.
     * The protected user accepts and a link is created with PENDING status.
     * Then the host shares the PIN code to confirm.
     *
     * @param command Data to accept the invitation
     * @return The created link (with PENDING status, awaiting PIN confirmation)
     */
    Link accept(AcceptInvitationCommand command);

    /**
     * Cancels an invitation.
     * Only the host can cancel their own invitations.
     *
     * @param invitationId The invitation ID
     * @param hostId The host user ID (must match)
     * @return The cancelled invitation
     */
    Invitation cancel(InvitationId invitationId, UserId hostId);

    /**
     * Gets all invitations created by a host.
     *
     * @param hostId The host user ID
     * @return List of invitations
     */
    List<Invitation> getByHost(UserId hostId);

    /**
     * Gets pending invitations created by a host.
     *
     * @param hostId The host user ID
     * @return List of pending invitations
     */
    List<Invitation> getPendingByHost(UserId hostId);

    /**
     * Command to create an invitation.
     */
    record CreateInvitationCommand(
        UserId hostId
    ) {
        public CreateInvitationCommand {
            if (hostId == null) {
                throw new IllegalArgumentException("Host ID is required");
            }
        }
    }

    /**
     * Command to accept an invitation.
     */
    record AcceptInvitationCommand(
        String token,
        UserId protectedUserId
    ) {
        public AcceptInvitationCommand {
            if (token == null || token.isBlank()) {
                throw new IllegalArgumentException("Token is required");
            }
            if (protectedUserId == null) {
                throw new IllegalArgumentException("Protected user ID is required");
            }
        }
    }
}
