package com.guardianapp.infrastructure.adapter.out.notification;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.guardianapp.domain.model.EmergencyAlert;
import com.guardianapp.domain.model.FamilyGroup;
import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.domain.port.out.DeviceTokenRepositoryPort;
import com.guardianapp.domain.port.out.EmergencyNotificationPort;
import com.guardianapp.domain.port.out.FamilyGroupRepositoryPort;
import com.guardianapp.domain.port.out.LinkRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Sends emergency notifications via WebSocket and FCM.
 */
@Component
public class EmergencyNotificationAdapter implements EmergencyNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(EmergencyNotificationAdapter.class);

    private final SimpMessagingTemplate messagingTemplate;
    private final DeviceTokenRepositoryPort deviceTokenRepository;
    private final LinkRepositoryPort linkRepository;
    private final FamilyGroupRepositoryPort familyGroupRepository;
    private final FirebaseMessaging firebaseMessaging;

    public EmergencyNotificationAdapter(
            SimpMessagingTemplate messagingTemplate,
            DeviceTokenRepositoryPort deviceTokenRepository,
            LinkRepositoryPort linkRepository,
            FamilyGroupRepositoryPort familyGroupRepository,
            ObjectProvider<FirebaseMessaging> firebaseMessagingProvider) {
        this.messagingTemplate = messagingTemplate;
        this.deviceTokenRepository = deviceTokenRepository;
        this.linkRepository = linkRepository;
        this.familyGroupRepository = familyGroupRepository;
        this.firebaseMessaging = firebaseMessagingProvider.getIfAvailable();
    }

    @Override
    public void notifyEmergencyTriggered(EmergencyAlert emergencyAlert) {
        EmergencyEvent event = EmergencyEvent.from(emergencyAlert, "EMERGENCY_TRIGGERED");

        Set<UserId> hostRecipients = resolveHostRecipients(emergencyAlert);
        for (UserId hostId : hostRecipients) {
            messagingTemplate.convertAndSend("/topic/host/" + hostId + "/emergencies", event);
            sendFcmToUser(
                    hostId,
                    "Emergencia activa",
                    "Tu familiar presiono el boton de emergencia.",
                    event);
        }

        messagingTemplate.convertAndSend(
                "/topic/protected/" + emergencyAlert.getProtectedUserId() + "/emergencies",
                event);
    }

    @Override
    public void notifyEmergencyResolved(EmergencyAlert emergencyAlert) {
        EmergencyEvent event = EmergencyEvent.from(emergencyAlert, "EMERGENCY_RESOLVED");

        Set<UserId> hostRecipients = resolveHostRecipients(emergencyAlert);
        for (UserId hostId : hostRecipients) {
            messagingTemplate.convertAndSend("/topic/host/" + hostId + "/emergencies", event);
            sendFcmToUser(
                    hostId,
                    "Emergencia resuelta",
                    "La emergencia fue resuelta por el anfitrion principal.",
                    event);
        }

        messagingTemplate.convertAndSend(
                "/topic/protected/" + emergencyAlert.getProtectedUserId() + "/emergencies",
                event);
    }

    private Set<UserId> resolveHostRecipients(EmergencyAlert emergencyAlert) {
        Set<UserId> hosts = resolveHostsFromFamilyGroup(emergencyAlert);
        if (!hosts.isEmpty()) {
            return hosts;
        }

        hosts.add(emergencyAlert.getPrimaryHostUserId());

        List<com.guardianapp.domain.model.Link> protectedLinks =
                linkRepository.findByProtected(emergencyAlert.getProtectedUserId());

        for (com.guardianapp.domain.model.Link link : protectedLinks) {
            if (link.isActive()) {
                hosts.add(link.getHostId());
            }
        }
        return hosts;
    }

    private Set<UserId> resolveHostsFromFamilyGroup(EmergencyAlert emergencyAlert) {
        Set<UserId> hosts = new HashSet<>();
        for (FamilyGroup group : familyGroupRepository.findByUserId(emergencyAlert.getProtectedUserId())) {
            if (group.getProtectedUserIds().stream().anyMatch(id -> id.equals(emergencyAlert.getProtectedUserId()))) {
                hosts.addAll(group.getHostUserIds());
            }
        }
        return hosts;
    }

    private void sendFcmToUser(UserId userId, String title, String body, EmergencyEvent event) {
        if (firebaseMessaging == null) {
            return;
        }

        for (String token : deviceTokenRepository.findTokensByUserId(userId)) {
            try {
                Message message = Message.builder()
                        .setToken(token)
                        .putData("type", event.type())
                        .putData("emergencyId", event.emergencyId())
                        .putData("linkId", event.linkId())
                        .putData("protectedUserId", event.protectedUserId())
                        .putData("primaryHostUserId", event.primaryHostUserId())
                        .putData("status", event.status())
                        .putData("latitude", event.latitude())
                        .putData("longitude", event.longitude())
                        .putData("title", title)
                        .putData("body", body)
                        .build();
                firebaseMessaging.send(message);
            } catch (Exception ex) {
                log.warn("Failed to send emergency FCM to token {}: {}", token, ex.getMessage());
            }
        }
    }

    private record EmergencyEvent(
            String type,
            String emergencyId,
            String linkId,
            String protectedUserId,
            String primaryHostUserId,
            String status,
            String latitude,
            String longitude
    ) {
        static EmergencyEvent from(EmergencyAlert alert, String type) {
            return new EmergencyEvent(
                    type,
                    alert.getId().toString(),
                    alert.getLinkId().toString(),
                    alert.getProtectedUserId().toString(),
                    alert.getPrimaryHostUserId().toString(),
                    alert.getStatus().name(),
                    String.valueOf(alert.getLatitude()),
                    String.valueOf(alert.getLongitude())
            );
        }
    }
}
