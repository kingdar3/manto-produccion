package com.guardianapp.infrastructure.adapter.out.notification;

import com.guardianapp.domain.model.IdentityVerification;
import com.guardianapp.domain.port.out.DeviceTokenRepositoryPort;
import com.guardianapp.domain.port.out.IdentityVerificationNotificationPort;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * Notification adapter for identity verification events.
 * Sends realtime updates over WebSocket and logs placeholders for FCM integration.
 */
@Component
public class IdentityVerificationNotificationAdapter implements IdentityVerificationNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(IdentityVerificationNotificationAdapter.class);

    private final SimpMessagingTemplate messagingTemplate;
    private final DeviceTokenRepositoryPort deviceTokenRepository;
    private final FirebaseMessaging firebaseMessaging;

    public IdentityVerificationNotificationAdapter(
            SimpMessagingTemplate messagingTemplate,
            DeviceTokenRepositoryPort deviceTokenRepository,
            ObjectProvider<FirebaseMessaging> firebaseMessagingProvider) {
        this.messagingTemplate = messagingTemplate;
        this.deviceTokenRepository = deviceTokenRepository;
        this.firebaseMessaging = firebaseMessagingProvider.getIfAvailable();
    }

    @Override
    public void notifyCreated(IdentityVerification verification) {
        VerificationEvent event = VerificationEvent.from(verification, "VERIFICATION_CREATED");
        messagingTemplate.convertAndSend(
            "/topic/host/" + verification.getHostUserId().getValue() + "/identity-verifications",
            event
        );

        sendFcmToUser(
            verification.getHostUserId(),
            "Solicitud de verificación",
            "Tu protegido solicita verificar una llamada",
            event
        );
    }

    @Override
    public void notifyResolved(IdentityVerification verification) {
        VerificationEvent event = VerificationEvent.from(verification, "VERIFICATION_RESOLVED");

        messagingTemplate.convertAndSend(
            "/topic/protected/" + verification.getProtectedUserId().getValue() + "/identity-verifications",
            event
        );
        messagingTemplate.convertAndSend(
            "/topic/host/" + verification.getHostUserId().getValue() + "/identity-verifications",
            event
        );

        sendFcmToUser(
            verification.getProtectedUserId(),
            "Resultado de verificación",
            "Tu familiar respondió: " + verification.getStatus().name(),
            event
        );
    }

    private void sendFcmToUser(
            com.guardianapp.domain.model.valueobject.UserId userId,
            String title,
            String body,
            VerificationEvent event) {
        if (firebaseMessaging == null) {
            log.debug("FirebaseMessaging not enabled; skipping FCM for user {}", userId);
            return;
        }

        for (String token : deviceTokenRepository.findTokensByUserId(userId)) {
            try {
                Message message = Message.builder()
                    .setToken(token)
                    .putData("type", event.type())
                    .putData("verificationId", event.verificationId())
                    .putData("linkId", event.linkId())
                    .putData("hostUserId", event.hostUserId())
                    .putData("protectedUserId", event.protectedUserId())
                    .putData("status", event.status())
                    .putData("challengeCode", event.challengeCode())
                    .putData("claimedPerson", event.claimedPerson())
                    .putData("resolutionNote", event.resolutionNote() == null ? "" : event.resolutionNote())
                    .putData("title", title)
                    .putData("body", body)
                    .build();
                firebaseMessaging.send(message);
            } catch (Exception ex) {
                log.warn("Failed to send FCM to token {}: {}", token, ex.getMessage());
            }
        }
    }

    private record VerificationEvent(
        String type,
        String verificationId,
        String linkId,
        String hostUserId,
        String protectedUserId,
        String status,
        String challengeCode,
        String claimedPerson,
        String resolutionNote
    ) {
        static VerificationEvent from(IdentityVerification verification, String type) {
            return new VerificationEvent(
                type,
                verification.getId().toString(),
                verification.getLinkId().toString(),
                verification.getHostUserId().toString(),
                verification.getProtectedUserId().toString(),
                verification.getStatus().name(),
                verification.getChallengeCode(),
                verification.getClaimedPerson(),
                verification.getResolutionNote()
            );
        }
    }
}
