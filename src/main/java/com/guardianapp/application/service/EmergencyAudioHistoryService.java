package com.guardianapp.application.service;

import com.guardianapp.domain.exception.EmergencyAudioException;
import com.guardianapp.domain.model.EmergencyAlert;
import com.guardianapp.domain.model.EmergencyAudioRecording;
import com.guardianapp.domain.model.FamilyGroup;
import com.guardianapp.domain.model.Link;
import com.guardianapp.domain.model.valueobject.EmergencyAlertId;
import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.domain.port.in.EmergencyAudioHistoryUseCase;
import com.guardianapp.domain.port.out.EmergencyAlertRepositoryPort;
import com.guardianapp.domain.port.out.EmergencyAudioRepositoryPort;
import com.guardianapp.domain.port.out.FamilyGroupRepositoryPort;
import com.guardianapp.domain.port.out.LinkRepositoryPort;

import java.util.List;

/**
 * Application service for emergency audio history retrieval.
 */
public class EmergencyAudioHistoryService implements EmergencyAudioHistoryUseCase {

    private final EmergencyAlertRepositoryPort emergencyAlertRepository;
    private final EmergencyAudioRepositoryPort emergencyAudioRepository;
    private final FamilyGroupRepositoryPort familyGroupRepository;
    private final LinkRepositoryPort linkRepository;

    public EmergencyAudioHistoryService(EmergencyAlertRepositoryPort emergencyAlertRepository,
                                        EmergencyAudioRepositoryPort emergencyAudioRepository,
                                        FamilyGroupRepositoryPort familyGroupRepository,
                                        LinkRepositoryPort linkRepository) {
        this.emergencyAlertRepository = emergencyAlertRepository;
        this.emergencyAudioRepository = emergencyAudioRepository;
        this.familyGroupRepository = familyGroupRepository;
        this.linkRepository = linkRepository;
    }

    @Override
    public List<EmergencyAudioRecording> getByEmergencyId(EmergencyAlertId emergencyAlertId, UserId requesterId) {
        EmergencyAlert alert = emergencyAlertRepository.findById(emergencyAlertId)
                .orElseThrow(() -> EmergencyAudioException.emergencyNotFound(emergencyAlertId.getValue().toString()));

        if (!isAuthorized(alert, requesterId)) {
            throw EmergencyAudioException.notAuthorized(requesterId.toString());
        }

        return emergencyAudioRepository.findByEmergencyAlertId(emergencyAlertId);
    }

    private boolean isAuthorized(EmergencyAlert alert, UserId userId) {
        if (alert.getProtectedUserId().equals(userId)) {
            return true;
        }
        if (alert.getPrimaryHostUserId().equals(userId)) {
            return true;
        }
        for (FamilyGroup group : familyGroupRepository.findByUserId(alert.getProtectedUserId())) {
            if (group.getProtectedUserIds().stream().anyMatch(id -> id.equals(alert.getProtectedUserId()))
                    && group.isHost(userId)) {
                return true;
            }
        }
        for (Link link : linkRepository.findByProtected(alert.getProtectedUserId())) {
            if (link.isActive() && link.getHostId().equals(userId)) {
                return true;
            }
        }
        return false;
    }
}
