package com.guardianapp.domain.port.in;

import com.guardianapp.domain.model.Link;
import com.guardianapp.domain.model.valueobject.LinkId;
import com.guardianapp.domain.model.valueobject.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Input port (Use Case) for querying links.
 * Defines the contract for link query operations.
 */
public interface QueryLinksUseCase {

    /**
     * Gets a link by its ID.
     *
     * @param linkId Link ID
     * @return Optional with link if exists
     */
    Optional<Link> getById(LinkId linkId);

    /**
     * Gets all links for a user (both as host and as protected).
     *
     * @param userId User ID
     * @return List of links involving the user
     */
    List<Link> getMyLinks(UserId userId);

    /**
     * Gets links where the user is the host.
     *
     * @param userId User ID
     * @return List of links where user is host
     */
    List<Link> getLinksAsHost(UserId userId);

    /**
     * Gets links where the user is protected.
     *
     * @param userId User ID
     * @return List of links where user is protected
     */
    List<Link> getLinksAsProtected(UserId userId);

    /**
     * Gets only active links for a user.
     *
     * @param userId User ID
     * @return List of active links
     */
    List<Link> getActiveLinks(UserId userId);

    /**
     * Gets pending links for a user.
     *
     * @param userId User ID
     * @return List of pending links
     */
    List<Link> getPendingLinks(UserId userId);
}
