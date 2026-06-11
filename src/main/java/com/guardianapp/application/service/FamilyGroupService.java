package com.guardianapp.application.service;

import com.guardianapp.domain.enums.FamilyMemberRole;
import com.guardianapp.domain.exception.FamilyGroupException;
import com.guardianapp.domain.exception.LinkException;
import com.guardianapp.domain.exception.UserException;
import com.guardianapp.domain.model.FamilyGroup;
import com.guardianapp.domain.model.FamilyGroupMember;
import com.guardianapp.domain.model.Link;
import com.guardianapp.domain.model.User;
import com.guardianapp.domain.model.valueobject.FamilyGroupId;
import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.domain.port.in.FamilyGroupUseCase;
import com.guardianapp.domain.port.out.FamilyGroupRepositoryPort;
import com.guardianapp.domain.port.out.LinkRepositoryPort;
import com.guardianapp.domain.port.out.UserRepositoryPort;

import java.util.List;
import java.util.Optional;

/**
 * Application service implementing family group use cases.
 */
public class FamilyGroupService implements FamilyGroupUseCase {

    private final FamilyGroupRepositoryPort familyGroupRepository;
    private final UserRepositoryPort userRepository;
    private final LinkRepositoryPort linkRepository;

    public FamilyGroupService(
            FamilyGroupRepositoryPort familyGroupRepository,
            UserRepositoryPort userRepository,
            LinkRepositoryPort linkRepository) {
        this.familyGroupRepository = familyGroupRepository;
        this.userRepository = userRepository;
        this.linkRepository = linkRepository;
    }

    @Override
    public FamilyGroup create(CreateFamilyGroupCommand command) {
        User host = requireActiveUser(command.primaryHostUserId());

        FamilyGroup familyGroup = FamilyGroup.create(command.name(), host.getId());
        return familyGroupRepository.save(familyGroup);
    }

    @Override
    public FamilyGroup addMember(AddFamilyMemberCommand command) {
        FamilyGroup group = familyGroupRepository.findById(command.familyGroupId())
                .orElseThrow(() -> FamilyGroupException.notFound(command.familyGroupId().toString()));

        requireActiveUser(command.requesterUserId());
        requireActiveUser(command.memberUserId());

        try {
            if (command.role() == FamilyMemberRole.PROTECTED) {
                group.addProtectedMember(command.requesterUserId(), command.memberUserId());
            } else if (command.role() == FamilyMemberRole.SECONDARY_HOST) {
                group.addSecondaryHost(command.requesterUserId(), command.memberUserId());
            } else {
                throw new IllegalArgumentException("Unsupported member role");
            }
        } catch (IllegalStateException ex) {
            throw mapStateException(command.memberUserId(), ex);
        }

        return familyGroupRepository.save(group);
    }

    @Override
    public FamilyGroup removeMember(RemoveFamilyMemberCommand command) {
        FamilyGroup group = familyGroupRepository.findById(command.familyGroupId())
                .orElseThrow(() -> FamilyGroupException.notFound(command.familyGroupId().toString()));

        try {
            group.removeMember(command.requesterUserId(), command.memberUserId());
        } catch (IllegalStateException ex) {
            throw mapStateException(command.memberUserId(), ex);
        }

        FamilyGroup saved = familyGroupRepository.save(group);
        cancelLinksForUser(command.memberUserId());
        return saved;
    }

    @Override
    public FamilyGroup leave(LeaveFamilyGroupCommand command) {
        FamilyGroup group = familyGroupRepository.findById(command.familyGroupId())
                .orElseThrow(() -> FamilyGroupException.notFound(command.familyGroupId().toString()));

        if (group.isPrimaryHost(command.memberUserId())) {
            cancelLinksForMembers(group.getMembers());
            familyGroupRepository.deleteById(command.familyGroupId());
            return group;
        }

        try {
            group.removeMember(command.memberUserId(), command.memberUserId());
        } catch (IllegalStateException ex) {
            throw mapStateException(command.memberUserId(), ex);
        }

        FamilyGroup saved = familyGroupRepository.save(group);
        cancelLinksForUser(command.memberUserId());
        return saved;
    }

    @Override
    public FamilyGroup rename(RenameFamilyGroupCommand command) {
        FamilyGroup group = familyGroupRepository.findById(command.familyGroupId())
                .orElseThrow(() -> FamilyGroupException.notFound(command.familyGroupId().toString()));

        try {
            group.rename(command.requesterUserId(), command.name());
        } catch (IllegalStateException ex) {
            throw FamilyGroupException.notAuthorized(command.requesterUserId().toString());
        }

        return familyGroupRepository.save(group);
    }

    @Override
    public void disband(DisbandFamilyGroupCommand command) {
        FamilyGroup group = familyGroupRepository.findById(command.familyGroupId())
                .orElseThrow(() -> FamilyGroupException.notFound(command.familyGroupId().toString()));
        if (!group.isPrimaryHost(command.requesterUserId())) {
            throw FamilyGroupException.notAuthorized(command.requesterUserId().toString());
        }

        cancelLinksForMembers(group.getMembers());
        familyGroupRepository.deleteById(command.familyGroupId());
    }

    @Override
    public Optional<FamilyGroup> getById(FamilyGroupId familyGroupId) {
        return familyGroupRepository.findById(familyGroupId);
    }

    @Override
    public List<FamilyGroup> getByUser(UserId userId) {
        return familyGroupRepository.findByUserId(userId);
    }

    private User requireActiveUser(UserId userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserException.notFound(userId.toString()));
        if (!user.isActive()) {
            throw UserException.userInactive(user.getName());
        }
        return user;
    }

    private void cancelLinksForMembers(List<FamilyGroupMember> members) {
        for (FamilyGroupMember member : members) {
            cancelLinksForUser(member.getUserId());
        }
    }

    private void cancelLinksForUser(UserId userId) {
        List<Link> links = linkRepository.findByUser(userId);
        for (Link link : links) {
            if (link.getStatus().isTerminalState()) {
                continue;
            }
            try {
                link.cancel();
                linkRepository.save(link);
            } catch (LinkException ignored) {
            }
        }
    }

    private RuntimeException mapStateException(UserId memberUserId, IllegalStateException ex) {
        String message = ex.getMessage() != null ? ex.getMessage() : "Family group operation failed";
        if (message.contains("already a member")) {
            return FamilyGroupException.memberAlreadyExists(memberUserId.toString());
        }
        if (message.contains("limit")) {
            return FamilyGroupException.secondaryHostLimitReached();
        }
        if (message.contains("Only primary host")) {
            return FamilyGroupException.notAuthorized(memberUserId.toString());
        }
        if (message.contains("Member not found")) {
            return FamilyGroupException.memberNotFound(memberUserId.toString());
        }
        return new IllegalArgumentException(message);
    }
}
