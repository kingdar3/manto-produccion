package com.guardianapp.application.service;

import com.guardianapp.domain.model.EmergencyAlert;
import com.guardianapp.domain.model.FamilyGroup;
import com.guardianapp.domain.model.Link;
import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.domain.port.in.EmergencyHistoryUseCase;
import com.guardianapp.domain.port.out.EmergencyAlertRepositoryPort;
import com.guardianapp.domain.port.out.FamilyGroupRepositoryPort;
import com.guardianapp.domain.port.out.LinkRepositoryPort;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Application service for emergency history retrieval.
 */
public class EmergencyHistoryService implements EmergencyHistoryUseCase {

    private final EmergencyAlertRepositoryPort emergencyAlertRepository;
    private final FamilyGroupRepositoryPort familyGroupRepository;
    private final LinkRepositoryPort linkRepository;

    public EmergencyHistoryService(EmergencyAlertRepositoryPort emergencyAlertRepository,
                                   FamilyGroupRepositoryPort familyGroupRepository,
                                   LinkRepositoryPort linkRepository) {
        this.emergencyAlertRepository = emergencyAlertRepository;
        this.familyGroupRepository = familyGroupRepository;
        this.linkRepository = linkRepository;
    }

    @Override
    public List<EmergencyAlert> getHistoryForHost(UserId hostId) {
        Map<String, EmergencyAlert> unique = new LinkedHashMap<>();

        for (EmergencyAlert alert : emergencyAlertRepository.findByHostUserId(hostId)) {
            unique.put(alert.getId().toString(), alert);
        }

        for (FamilyGroup group : familyGroupRepository.findByUserId(hostId)) {
            if (!group.isHost(hostId)) {
                continue;
            }
            for (UserId protectedUserId : group.getProtectedUserIds()) {
                for (EmergencyAlert alert : emergencyAlertRepository.findByProtectedUserId(protectedUserId)) {
                    unique.put(alert.getId().toString(), alert);
                }
            }
        }

        for (Link link : linkRepository.findByHost(hostId)) {
            for (EmergencyAlert alert : emergencyAlertRepository.findByProtectedUserId(link.getProtectedId())) {
                unique.put(alert.getId().toString(), alert);
            }
        }

        return unique.values().stream().toList();
    }
}
