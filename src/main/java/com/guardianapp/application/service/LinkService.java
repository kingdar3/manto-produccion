package com.guardianapp.application.service;

import com.guardianapp.domain.enums.LinkStatus;
import com.guardianapp.domain.exception.UserException;
import com.guardianapp.domain.exception.LinkException;
import com.guardianapp.domain.model.User;
import com.guardianapp.domain.model.Link;
import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.domain.model.valueobject.LinkId;
import com.guardianapp.domain.port.in.CreateLinkUseCase;
import com.guardianapp.domain.port.in.QueryLinksUseCase;
import com.guardianapp.domain.port.out.UserRepositoryPort;
import com.guardianapp.domain.port.out.LinkRepositoryPort;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Application service that implements link use cases.
 * Orchestrates business logic and coordinates with output ports.
 * 
 * Note: Users don't have fixed roles. Any active user can be HOST or PROTECTED
 * in different links simultaneously.
 */
public class LinkService implements CreateLinkUseCase, QueryLinksUseCase {

    private final LinkRepositoryPort linkRepository;
    private final UserRepositoryPort userRepository;

    public LinkService(LinkRepositoryPort linkRepository, 
                       UserRepositoryPort userRepository) {
        this.linkRepository = linkRepository;
        this.userRepository = userRepository;
    }

    // ==================== CreateLinkUseCase ====================

    @Override
    public Link createRequest(CreateLinkCommand command) {
        // Validate host exists and is active
        User host = userRepository.findById(command.hostId())
            .orElseThrow(() -> UserException.notFound(command.hostId().toString()));

        if (!host.canParticipateInLinks()) {
            throw UserException.userInactive(host.getName());
        }

        // Validate protected exists and is active
        User protectedUser = userRepository.findById(command.protectedId())
            .orElseThrow(() -> UserException.notFound(command.protectedId().toString()));

        if (!protectedUser.canParticipateInLinks()) {
            throw UserException.userInactive(protectedUser.getName());
        }

        // Check no active or pending link exists between both (in any direction)
        if (linkRepository.existsActiveOrPending(
                command.hostId(), command.protectedId())) {
            throw LinkException.alreadyExists();
        }

        // Create domain link
        Link link = Link.createRequest(
            command.hostId(),
            command.protectedId()
        );

        // Persist and return
        return linkRepository.save(link);
    }

    @Override
    public Link confirm(ConfirmLinkCommand command) {
        // Find link
        Link link = linkRepository.findById(command.linkId())
            .orElseThrow(() -> LinkException.notFound(command.linkId().toString()));

        // Verify the one confirming is the protected user
        if (!link.isProtected(command.protectedId())) {
            throw LinkException.operationNotAllowed(
                "Only the protected user can confirm the link"
            );
        }

        // Confirm link (validations are in domain)
        link.confirm(command.connectionCode());

        // Persist and return
        return linkRepository.save(link);
    }

    @Override
    public Link reject(LinkId linkId, UserId userId) {
        Link link = linkRepository.findById(linkId)
            .orElseThrow(() -> LinkException.notFound(linkId.toString()));

        // Verify the one rejecting is the protected user
        if (!link.isProtected(userId)) {
            throw LinkException.operationNotAllowed(
                "Only the protected user can reject the link"
            );
        }

        // Reject link
        link.reject();

        return linkRepository.save(link);
    }

    @Override
    public Link cancel(LinkId linkId, UserId userId) {
        Link link = linkRepository.findById(linkId)
            .orElseThrow(() -> LinkException.notFound(linkId.toString()));

        // Verify the one cancelling is part of the link
        if (!link.involvesUser(userId)) {
            throw LinkException.operationNotAllowed(
                "Only link participants can cancel it"
            );
        }

        // Cancel link
        link.cancel();

        return linkRepository.save(link);
    }

    // ==================== QueryLinksUseCase ====================

    @Override
    public Optional<Link> getById(LinkId linkId) {
        return linkRepository.findById(linkId);
    }

    @Override
    public List<Link> getMyLinks(UserId userId) {
        return linkRepository.findByUser(userId);
    }

    @Override
    public List<Link> getLinksAsHost(UserId userId) {
        return linkRepository.findByHost(userId);
    }

    @Override
    public List<Link> getLinksAsProtected(UserId userId) {
        return linkRepository.findByProtected(userId);
    }

    @Override
    public List<Link> getActiveLinks(UserId userId) {
        return linkRepository.findByUser(userId).stream()
            .filter(link -> link.getStatus() == LinkStatus.ACTIVE)
            .collect(Collectors.toList());
    }

    @Override
    public List<Link> getPendingLinks(UserId userId) {
        return linkRepository.findByUser(userId).stream()
            .filter(link -> link.getStatus() == LinkStatus.PENDING)
            .collect(Collectors.toList());
    }
}
