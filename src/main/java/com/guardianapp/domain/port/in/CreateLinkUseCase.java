package com.guardianapp.domain.port.in;

import com.guardianapp.domain.model.Link;
import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.domain.model.valueobject.LinkId;

/**
 * Input port (Use Case) for creating and managing links.
 * Defines the contract that the application layer must implement.
 */
public interface CreateLinkUseCase {

    /**
     * Creates a new link request.
     * The host initiates the request and a code is generated for the protected user.
     *
     * @param command Data to create the link
     * @return The created link with PENDING status
     */
    Link createRequest(CreateLinkCommand command);

    /**
     * Confirms a link using the connection code.
     * The protected user enters the code to activate the link.
     *
     * @param command Data to confirm the link
     * @return The confirmed link with ACTIVE status
     */
    Link confirm(ConfirmLinkCommand command);

    /**
     * Rejects a link request.
     *
     * @param linkId ID of the link to reject
     * @param userId ID of the user rejecting (must be the protected user)
     * @return The rejected link
     */
    Link reject(LinkId linkId, UserId userId);

    /**
     * Cancels an active or pending link.
     *
     * @param linkId ID of the link to cancel
     * @param userId ID of the user cancelling
     * @return The cancelled link
     */
    Link cancel(LinkId linkId, UserId userId);

    /**
     * Command to create a link request.
     */
    record CreateLinkCommand(
        UserId hostId,
        UserId protectedId
    ) {
        public CreateLinkCommand {
            if (hostId == null) {
                throw new IllegalArgumentException("Host ID is required");
            }
            if (protectedId == null) {
                throw new IllegalArgumentException("Protected ID is required");
            }
        }
    }

    /**
     * Command to confirm a link.
     */
    record ConfirmLinkCommand(
        LinkId linkId,
        UserId protectedId,
        String connectionCode
    ) {
        public ConfirmLinkCommand {
            if (linkId == null) {
                throw new IllegalArgumentException("Link ID is required");
            }
            if (protectedId == null) {
                throw new IllegalArgumentException("Protected ID is required");
            }
            if (connectionCode == null || connectionCode.isBlank()) {
                throw new IllegalArgumentException("Connection code is required");
            }
        }
    }
}
