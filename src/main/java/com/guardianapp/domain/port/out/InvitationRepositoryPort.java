package com.guardianapp.domain.port.out;

import com.guardianapp.domain.enums.InvitationStatus;
import com.guardianapp.domain.model.Invitation;
import com.guardianapp.domain.model.valueobject.InvitationId;
import com.guardianapp.domain.model.valueobject.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Output port for invitation persistence.
 * Defines the contract that persistence adapters must implement.
 */
public interface InvitationRepositoryPort {

    /**
     * Saves an invitation (create or update).
     *
     * @param invitation Invitation to save
     * @return Saved invitation
     */
    Invitation save(Invitation invitation);

    /**
     * Finds an invitation by ID.
     *
     * @param id Invitation ID
     * @return Optional with invitation if exists
     */
    Optional<Invitation> findById(InvitationId id);

    /**
     * Finds an invitation by its token.
     *
     * @param token Invitation token
     * @return Optional with invitation if exists
     */
    Optional<Invitation> findByToken(String token);

    /**
     * Finds all invitations created by a host.
     *
     * @param hostId Host user ID
     * @return List of invitations
     */
    List<Invitation> findByHost(UserId hostId);

    /**
     * Finds invitations by host and status.
     *
     * @param hostId Host user ID
     * @param status Invitation status
     * @return List of invitations
     */
    List<Invitation> findByHostAndStatus(UserId hostId, InvitationStatus status);

    /**
     * Finds all pending invitations that have expired.
     * Used for cleanup/expiration jobs.
     *
     * @return List of expired pending invitations
     */
    List<Invitation> findExpiredPending();

    /**
     * Deletes an invitation by ID.
     *
     * @param id Invitation ID to delete
     */
    void delete(InvitationId id);

    /**
     * Checks if a token already exists.
     *
     * @param token Token to check
     * @return true if exists
     */
    boolean existsByToken(String token);
}
