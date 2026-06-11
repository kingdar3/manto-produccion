package com.guardianapp.domain.port.out;

import com.guardianapp.domain.enums.LinkStatus;
import com.guardianapp.domain.model.Link;
import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.domain.model.valueobject.LinkId;

import java.util.List;
import java.util.Optional;

/**
 * Output port for link persistence.
 * Defines the contract that persistence adapters must implement.
 */
public interface LinkRepositoryPort {

    /**
     * Saves a link (create or update).
     *
     * @param link Link to save
     * @return Saved link
     */
    Link save(Link link);

    /**
     * Finds a link by ID.
     *
     * @param id Link ID
     * @return Optional with link if exists
     */
    Optional<Link> findById(LinkId id);

    /**
     * Finds links where the user is host.
     *
     * @param hostId Host ID
     * @return List of links
     */
    List<Link> findByHost(UserId hostId);

    /**
     * Finds links where the user is protected.
     *
     * @param protectedId Protected ID
     * @return List of links
     */
    List<Link> findByProtected(UserId protectedId);

    /**
     * Finds all links involving a user.
     *
     * @param userId User ID
     * @return List of links
     */
    List<Link> findByUser(UserId userId);

    /**
     * Finds links between two specific users.
     *
     * @param hostId Host ID
     * @param protectedId Protected ID
     * @return List of links between both users
     */
    List<Link> findByHostAndProtected(UserId hostId, UserId protectedId);

    /**
     * Checks if an active or pending link exists between two users.
     *
     * @param hostId Host ID
     * @param protectedId Protected ID
     * @return true if active or pending link exists
     */
    boolean existsActiveOrPending(UserId hostId, UserId protectedId);

    /**
     * Finds links by status.
     *
     * @param status Link status
     * @return List of links with that status
     */
    List<Link> findByStatus(LinkStatus status);

    /**
     * Finds active links for a host.
     *
     * @param hostId Host ID
     * @return List of active links
     */
    List<Link> findActiveByHost(UserId hostId);

    List<Link> findActiveByProtected(UserId protectedId);

    /**
     * Deletes a link by ID.
     *
     * @param id Link ID to delete
     */
    void delete(LinkId id);
}
