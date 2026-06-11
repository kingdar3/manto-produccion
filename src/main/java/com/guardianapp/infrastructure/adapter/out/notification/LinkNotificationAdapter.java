package com.guardianapp.infrastructure.adapter.out.notification;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.guardianapp.domain.model.Link;
import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.domain.port.out.DeviceTokenRepositoryPort;
import com.guardianapp.domain.port.out.LinkNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * Sends link lifecycle notifications via WebSocket and FCM.
 */
@Component
public class LinkNotificationAdapter implements LinkNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(LinkNotificationAdapter.class);

    private final SimpMessagingTemplate messagingTemplate;
    private final DeviceTokenRepositoryPort deviceTokenRepository;
    private final FirebaseMessaging firebaseMessaging;

    public LinkNotificationAdapter(
            SimpMessagingTemplate messagingTemplate,
            DeviceTokenRepositoryPort deviceTokenRepository,
            ObjectProvider<FirebaseMessaging> firebaseMessagingProvider) {
        this.messagingTemplate = messagingTemplate;
        this.deviceTokenRepository = deviceTokenRepository;
        this.firebaseMessaging = firebaseMessagingProvider.getIfAvailable();
    }

    @Override
    public void notifyLinkPending(Link link) {
        notifyLinkActivated(link);
    }

    @Override
    public void notifyLinkActivated(Link link) {
        LinkEvent event = LinkEvent.from(link, "LINK_ACTIVATED");
        messagingTemplate.convertAndSend("/topic/host/" + link.getHostId() + "/links", event);
        messagingTemplate.convertAndSend("/topic/protected/" + link.getProtectedId() + "/links", event);

        sendFcmToUser(link.getHostId(), "Vínculo activado", "El vínculo quedó activo correctamente.", event);
        sendFcmToUser(link.getProtectedId(), "Protección activada", "La vinculación se completó con éxito.", event);
    }

    private void sendFcmToUser(UserId userId, String title, String body, LinkEvent event) {
        if (firebaseMessaging == null) {
            return;
        }
        for (String token : deviceTokenRepository.findTokensByUserId(userId)) {
            try {
                Message message = Message.builder()
                    .setToken(token)
                    .putData("type", event.type())
                    .putData("linkId", event.linkId())
                    .putData("hostUserId", event.hostUserId())
                    .putData("protectedUserId", event.protectedUserId())
                    .putData("status", event.status())
                    .putData("title", title)
                    .putData("body", body)
                    .build();
                firebaseMessaging.send(message);
            } catch (Exception ex) {
                log.warn("Failed to send link FCM to token {}: {}", token, ex.getMessage());
            }
        }
    }

    private record LinkEvent(
        String type,
        String linkId,
        String hostUserId,
        String protectedUserId,
        String status
    ) {
        static LinkEvent from(Link link, String type) {
            return new LinkEvent(
                type,
                link.getId().toString(),
                link.getHostId().toString(),
                link.getProtectedId().toString(),
                link.getStatus().name()
            );
        }
    }
}
