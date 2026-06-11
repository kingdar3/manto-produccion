package com.guardianapp.application.service;

import com.guardianapp.domain.exception.EmergencyAlertException;
import com.guardianapp.domain.model.EmergencyAlert;
import com.guardianapp.domain.model.FamilyGroup;
import com.guardianapp.domain.model.Link;
import com.guardianapp.domain.model.valueobject.EmergencyAlertId;
import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.domain.port.in.EmergencyAlertUseCase;
import com.guardianapp.domain.port.out.EmergencyAlertRepositoryPort;
import com.guardianapp.domain.port.out.FamilyGroupRepositoryPort;
import com.guardianapp.domain.port.out.EmergencyNotificationPort;
import com.guardianapp.domain.port.out.LinkRepositoryPort;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Application service implementing emergency alert use cases.
 */
public class EmergencyAlertService implements EmergencyAlertUseCase {

    private final EmergencyAlertRepositoryPort emergencyAlertRepository;
    private final LinkRepositoryPort linkRepository;
    private final EmergencyNotificationPort emergencyNotificationPort;
    private final FamilyGroupRepositoryPort familyGroupRepository;

    public EmergencyAlertService(
            EmergencyAlertRepositoryPort emergencyAlertRepository,
            LinkRepositoryPort linkRepository,
            EmergencyNotificationPort emergencyNotificationPort,
            FamilyGroupRepositoryPort familyGroupRepository) {
        this.emergencyAlertRepository = emergencyAlertRepository;
        this.linkRepository = linkRepository;
        this.emergencyNotificationPort = emergencyNotificationPort;
        this.familyGroupRepository = familyGroupRepository;
    }

    @Override
    public EmergencyAlert trigger(TriggerEmergencyCommand command) {
        Link link = linkRepository.findById(command.linkId())
                .orElseThrow(() -> EmergencyAlertException.linkNotFound(command.linkId().toString()));

        if (!link.isActive()) {
            throw EmergencyAlertException.linkNotActive(command.linkId().toString());
        }

        if (!link.isProtected(command.protectedUserId())) {
            throw EmergencyAlertException.protectedUserNotInLink(
                    command.protectedUserId().toString(),
                    command.linkId().toString());
        }

        if (!isProtectedStillInHostFamily(link.getHostId(), command.protectedUserId())) {
            throw EmergencyAlertException.protectedUserNotInFamily(
                    command.protectedUserId().toString(),
                    link.getHostId().toString());
        }

        EmergencyAlert emergencyAlert = EmergencyAlert.create(
                command.linkId(),
                command.protectedUserId(),
                link.getHostId(),
                command.latitude(),
                command.longitude());

        EmergencyAlert saved = emergencyAlertRepository.save(emergencyAlert);
        emergencyNotificationPort.notifyEmergencyTriggered(saved);
        return saved;
    }

    @Override
    public EmergencyAlert resolve(ResolveEmergencyCommand command) {
        EmergencyAlert alert = emergencyAlertRepository.findById(command.emergencyAlertId())
                .orElseThrow(() -> EmergencyAlertException.notFound(command.emergencyAlertId().toString()));

        if (alert.isResolved()) {
            throw EmergencyAlertException.alreadyResolved(command.emergencyAlertId().toString());
        }

        if (!alert.getPrimaryHostUserId().equals(command.hostUserId())) {
            throw EmergencyAlertException.notAuthorizedToResolve(command.hostUserId().toString());
        }

        alert.resolve(command.hostUserId(), command.resolutionType(), command.note());
        EmergencyAlert saved = emergencyAlertRepository.save(alert);
        emergencyNotificationPort.notifyEmergencyResolved(saved);
        return saved;
    }

    @Override
    public Optional<EmergencyAlert> getById(EmergencyAlertId alertId) {
        return emergencyAlertRepository.findById(alertId);
    }

    @Override
    public List<EmergencyAlert> getActiveForHost(UserId hostId) {
        Map<String, EmergencyAlert> unique = new LinkedHashMap<>();

        for (EmergencyAlert alert : emergencyAlertRepository.findActiveByHostId(hostId)) {
            unique.put(alert.getId().toString(), alert);
        }

        for (FamilyGroup group : familyGroupRepository.findByUserId(hostId)) {
            if (!group.isHost(hostId)) {
                continue;
            }
            for (UserId protectedUserId : group.getProtectedUserIds()) {
                for (EmergencyAlert alert : emergencyAlertRepository.findActiveByProtectedUserId(protectedUserId)) {
                    unique.put(alert.getId().toString(), alert);
                }
            }
        }

        return unique.values().stream().toList();
    }

    @Override
    public List<EmergencyAlert> getActiveForProtected(UserId protectedUserId) {
        return emergencyAlertRepository.findActiveByProtectedUserId(protectedUserId);
    }

    @Override
    public List<EmergencyAlert> getByProtectedUser(UserId protectedUserId) {
        return emergencyAlertRepository.findByProtectedUserId(protectedUserId);
    }

    private boolean isProtectedStillInHostFamily(UserId hostUserId, UserId protectedUserId) {
        List<FamilyGroup> groups = familyGroupRepository.findByUserId(protectedUserId);
        for (FamilyGroup group : groups) {
            if (group.hasMember(protectedUserId) && group.isHost(hostUserId)) {
                return true;
            }
        }
        return false;
    }
}
