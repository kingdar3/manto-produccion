package com.guardianapp.application.service;

import com.guardianapp.domain.enums.FamilyInvitationStatus;
import com.guardianapp.domain.enums.FamilyMemberRole;
import com.guardianapp.domain.exception.FamilyGroupException;
import com.guardianapp.domain.exception.FamilyInvitationException;
import com.guardianapp.domain.model.FamilyGroup;
import com.guardianapp.domain.model.FamilyInvitation;
import com.guardianapp.domain.model.Link;
import com.guardianapp.domain.model.User;
import com.guardianapp.domain.model.valueobject.FamilyGroupId;
import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.domain.port.in.FamilyInvitationUseCase;
import com.guardianapp.domain.port.out.FamilyGroupRepositoryPort;
import com.guardianapp.domain.port.out.FamilyInvitationRepositoryPort;
import com.guardianapp.domain.port.out.LinkNotificationPort;
import com.guardianapp.domain.port.out.LinkRepositoryPort;
import com.guardianapp.domain.port.out.UserRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Application service implementing family invitation use cases.
 */
public class FamilyInvitationService implements FamilyInvitationUseCase {

    private static final Logger log = LoggerFactory.getLogger(FamilyInvitationService.class);

    private final FamilyInvitationRepositoryPort familyInvitationRepository;
    private final FamilyGroupRepositoryPort familyGroupRepository;
    private final UserRepositoryPort userRepository;
    private final LinkRepositoryPort linkRepository;
    private final LinkNotificationPort linkNotificationPort;

    public FamilyInvitationService(
            FamilyInvitationRepositoryPort familyInvitationRepository,
            FamilyGroupRepositoryPort familyGroupRepository,
            UserRepositoryPort userRepository,
            LinkRepositoryPort linkRepository,
            LinkNotificationPort linkNotificationPort) {
        this.familyInvitationRepository = familyInvitationRepository;
        this.familyGroupRepository = familyGroupRepository;
        this.userRepository = userRepository;
        this.linkRepository = linkRepository;
        this.linkNotificationPort = linkNotificationPort;
    }

    @Override
    public FamilyInvitation create(CreateFamilyInvitationCommand command) {
        FamilyGroup group = familyGroupRepository.findById(command.familyGroupId())
                .orElseThrow(() -> FamilyGroupException.notFound(command.familyGroupId().toString()));

        if (!group.isPrimaryHost(command.inviterUserId())) {
            throw FamilyInvitationException.notAuthorized(command.inviterUserId().toString());
        }

        FamilyInvitation invitation = FamilyInvitation.create(
                command.familyGroupId(),
                command.inviterUserId(),
                command.role()
        );
        return familyInvitationRepository.save(invitation);
    }

    @Override
    public Optional<FamilyInvitation> getByToken(String token) {
        Optional<FamilyInvitation> invitation = familyInvitationRepository.findByToken(token);
        invitation.ifPresent(inv -> {
            if (inv.isExpired() && inv.getStatus() == FamilyInvitationStatus.PENDING) {
                inv.markExpired();
                familyInvitationRepository.save(inv);
            }
        });
        return invitation;
    }

    @Override
    @Transactional
    public FamilyGroup accept(AcceptFamilyInvitationCommand command) {
        FamilyInvitation invitation = familyInvitationRepository.findByToken(command.token())
                .orElseThrow(() -> FamilyInvitationException.notFound(command.token()));

        if (invitation.isExpired()) {
            invitation.markExpired();
            familyInvitationRepository.save(invitation);
            throw FamilyInvitationException.expired(command.token());
        }
        if (invitation.getStatus() == FamilyInvitationStatus.CANCELLED) {
            throw FamilyInvitationException.cancelled(command.token());
        }
        if (invitation.getStatus() == FamilyInvitationStatus.ACCEPTED) {
            throw FamilyInvitationException.alreadyAccepted(command.token());
        }

        User acceptedUser = userRepository.findById(command.acceptedByUserId())
                .orElseThrow(() -> FamilyGroupException.userNotFound(command.acceptedByUserId().toString()));
        if (!acceptedUser.isActive()) {
            throw FamilyGroupException.userNotFound(command.acceptedByUserId().toString());
        }

        FamilyGroup group = familyGroupRepository.findById(invitation.getFamilyGroupId())
                .orElseThrow(() -> FamilyGroupException.notFound(invitation.getFamilyGroupId().toString()));

        boolean groupChanged = false;
        if (!group.hasMember(command.acceptedByUserId())) {
            FamilyMemberRole role = invitation.getTargetRole();
            if (role == FamilyMemberRole.PROTECTED) {
                group.addProtectedMember(invitation.getInvitedByUserId(), command.acceptedByUserId());
                groupChanged = true;
            } else {
                group.addSecondaryHost(invitation.getInvitedByUserId(), command.acceptedByUserId());
                groupChanged = true;
            }
        }

        invitation.accept(command.acceptedByUserId());
        familyInvitationRepository.save(invitation);

        FamilyGroup savedGroup = groupChanged ? familyGroupRepository.save(group) : group;

        if (invitation.getTargetRole() == FamilyMemberRole.PROTECTED) {
            UserId hostId = savedGroup.getPrimaryHostUserId();
            UserId protectedId = command.acceptedByUserId();
            if (!hostId.equals(protectedId) && !linkRepository.existsActiveOrPending(hostId, protectedId)) {
                Link link = Link.createRequest(hostId, protectedId);
                Link savedLink = linkRepository.save(link);
                try {
                    linkNotificationPort.notifyLinkPending(savedLink);
                } catch (Exception ex) {
                    log.warn("Family invitation accepted but link notification failed: {}", ex.getMessage());
                }
            }
        }

        return savedGroup;
    }

    @Override
    public FamilyInvitation cancel(CancelFamilyInvitationCommand command) {
        FamilyInvitation invitation = familyInvitationRepository.findByToken(command.token())
                .orElseThrow(() -> FamilyInvitationException.notFound(command.token()));

        invitation.cancel(command.requesterUserId());
        return familyInvitationRepository.save(invitation);
    }

    @Override
    public List<FamilyInvitation> getByFamilyGroup(FamilyGroupId familyGroupId) {
        return familyInvitationRepository.findByFamilyGroupId(familyGroupId);
    }
}
