package com.guardianapp.application.service;

import com.guardianapp.domain.enums.InvitationStatus;
import com.guardianapp.domain.exception.InvitationException;
import com.guardianapp.domain.exception.LinkException;
import com.guardianapp.domain.exception.UserException;
import com.guardianapp.domain.model.FamilyGroup;
import com.guardianapp.domain.model.Invitation;
import com.guardianapp.domain.model.Link;
import com.guardianapp.domain.model.User;
import com.guardianapp.domain.model.valueobject.InvitationId;
import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.domain.port.in.InvitationUseCase;
import com.guardianapp.domain.port.out.FamilyGroupRepositoryPort;
import com.guardianapp.domain.port.out.InvitationRepositoryPort;
import com.guardianapp.domain.port.out.LinkNotificationPort;
import com.guardianapp.domain.port.out.LinkRepositoryPort;
import com.guardianapp.domain.port.out.UserRepositoryPort;

import java.util.List;
import java.util.Optional;

/**
 * Application service that implements invitation use cases.
 * Manages the invitation flow for creating links between users.
 */
public class InvitationService implements InvitationUseCase {

    private final InvitationRepositoryPort invitationRepository;
    private final UserRepositoryPort userRepository;
    private final LinkRepositoryPort linkRepository;
    private final LinkNotificationPort linkNotificationPort;
    private final FamilyGroupRepositoryPort familyGroupRepository;

    public InvitationService(InvitationRepositoryPort invitationRepository,
                             UserRepositoryPort userRepository,
                             LinkRepositoryPort linkRepository,
                             LinkNotificationPort linkNotificationPort,
                             FamilyGroupRepositoryPort familyGroupRepository) {
        this.invitationRepository = invitationRepository;
        this.userRepository = userRepository;
        this.linkRepository = linkRepository;
        this.linkNotificationPort = linkNotificationPort;
        this.familyGroupRepository = familyGroupRepository;
    }

    @Override
    public Invitation create(CreateInvitationCommand command) {
        // Validate host exists and is active
        User host = userRepository.findById(command.hostId())
            .orElseThrow(() -> InvitationException.hostNotFound(command.hostId().toString()));

        if (!host.canParticipateInLinks()) {
            throw UserException.userInactive(host.getName());
        }

        // Create invitation with host name for display
        Invitation invitation = Invitation.create(command.hostId(), host.getName());

        // Persist and return
        return invitationRepository.save(invitation);
    }

    @Override
    public Optional<Invitation> getByToken(String token) {
        Optional<Invitation> invitationOpt = invitationRepository.findByToken(token);
        
        // Check if expired and update status if needed
        invitationOpt.ifPresent(invitation -> {
            if (invitation.isExpired() && invitation.getStatus() == InvitationStatus.PENDING) {
                invitation.markAsExpired();
                invitationRepository.save(invitation);
            }
        });
        
        return invitationOpt;
    }

    @Override
    public Optional<Invitation> getById(InvitationId invitationId) {
        return invitationRepository.findById(invitationId);
    }

    @Override
    public Link accept(AcceptInvitationCommand command) {
        // Find invitation by token
        Invitation invitation = invitationRepository.findByToken(command.token())
            .orElseThrow(() -> InvitationException.notFound(command.token()));

        // Validate protected user exists and is active
        User protectedUser = userRepository.findById(command.protectedUserId())
            .orElseThrow(() -> UserException.notFound(command.protectedUserId().toString()));

        if (!protectedUser.canParticipateInLinks()) {
            throw UserException.userInactive(protectedUser.getName());
        }

        // Cannot accept your own invitation
        if (invitation.getHostId().equals(command.protectedUserId())) {
            throw InvitationException.cannotInviteSelf();
        }

        // Check invitation is still valid
        if (invitation.isExpired()) {
            invitation.markAsExpired();
            invitationRepository.save(invitation);
            throw InvitationException.expired(command.token());
        }

        if (invitation.getStatus() == InvitationStatus.ACCEPTED) {
            throw InvitationException.alreadyAccepted(command.token());
        }

        if (invitation.getStatus() == InvitationStatus.CANCELLED) {
            throw InvitationException.cancelled(command.token());
        }

        // Check no active or pending link exists between both users
        if (linkRepository.existsActiveOrPending(invitation.getHostId(), command.protectedUserId())) {
            throw LinkException.alreadyExists();
        }

        // Accept the invitation
        invitation.accept(command.protectedUserId());
        invitationRepository.save(invitation);

        // Create an active link (PIN removed; single-step confirmation)
        Link link = Link.createRequest(
            invitation.getHostId(),
            command.protectedUserId()
        );

        // Persist and return the link
        Link saved = linkRepository.save(link);
        linkNotificationPort.notifyLinkActivated(saved);

        // Auto-enroll protected user into host's primary family group when available
        List<FamilyGroup> hostGroups = familyGroupRepository.findByPrimaryHostUserId(invitation.getHostId());
        if (!hostGroups.isEmpty()) {
            FamilyGroup group = hostGroups.get(0);
            if (!group.hasMember(command.protectedUserId())) {
                group.addProtectedMember(invitation.getHostId(), command.protectedUserId());
                familyGroupRepository.save(group);
            }
        }

        return saved;
    }

    @Override
    public Invitation cancel(InvitationId invitationId, UserId hostId) {
        Invitation invitation = invitationRepository.findById(invitationId)
            .orElseThrow(() -> InvitationException.notFoundById(invitationId.toString()));

        // Cancel (validates host ownership internally)
        invitation.cancel(hostId);

        return invitationRepository.save(invitation);
    }

    @Override
    public List<Invitation> getByHost(UserId hostId) {
        return invitationRepository.findByHost(hostId);
    }

    @Override
    public List<Invitation> getPendingByHost(UserId hostId) {
        return invitationRepository.findByHostAndStatus(hostId, InvitationStatus.PENDING);
    }
}
