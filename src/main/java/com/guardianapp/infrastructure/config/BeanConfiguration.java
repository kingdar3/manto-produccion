package com.guardianapp.infrastructure.config;

import com.guardianapp.application.service.AlertService;
import com.guardianapp.application.service.EmergencyAlertService;
import com.guardianapp.application.service.EmergencyAudioService;
import com.guardianapp.application.service.EmergencyAudioHistoryService;
import com.guardianapp.application.service.EmergencyHistoryService;
import com.guardianapp.application.service.FamilyGroupService;
import com.guardianapp.application.service.FamilyInvitationService;
import com.guardianapp.application.service.IdentityVerificationService;
import com.guardianapp.application.service.InvitationService;
import com.guardianapp.application.service.NotificationTokenService;
import com.guardianapp.application.service.SmsThreatAlertService;
import com.guardianapp.application.service.ThreatAnalysisService;
import com.guardianapp.application.service.UserService;
import com.guardianapp.application.service.LinkService;
import com.guardianapp.domain.port.out.DeviceTokenRepositoryPort;
import com.guardianapp.domain.port.out.EmergencyAlertRepositoryPort;
import com.guardianapp.domain.port.out.EmergencyAudioRepositoryPort;
import com.guardianapp.domain.port.out.EmergencyAudioStoragePort;
import com.guardianapp.domain.port.out.EmergencyNotificationPort;
import com.guardianapp.domain.port.out.FamilyGroupRepositoryPort;
import com.guardianapp.domain.port.out.FamilyInvitationRepositoryPort;
import com.guardianapp.domain.port.out.IdentityVerificationNotificationPort;
import com.guardianapp.domain.port.out.IdentityVerificationRepositoryPort;
import com.guardianapp.domain.port.out.AlertRepositoryPort;
import com.guardianapp.domain.port.out.InvitationRepositoryPort;
import com.guardianapp.domain.port.out.LinkNotificationPort;
import com.guardianapp.domain.port.out.UserRepositoryPort;
import com.guardianapp.domain.port.out.LinkRepositoryPort;
import com.guardianapp.domain.port.out.SafeBrowsingPort;
import com.guardianapp.domain.port.out.SmsThreatAlertRepositoryPort;
import com.guardianapp.domain.port.out.TrustedDomainRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Bean configuration for dependency injection.
 * Maintains separation between hexagonal architecture layers.
 */
@Configuration
public class BeanConfiguration {

    /**
     * Creates the UserService singleton.
     * This service implements multiple use cases.
     */
    @Bean
    public UserService userService(UserRepositoryPort userRepository) {
        return new UserService(userRepository);
    }

    /**
     * Creates the LinkService singleton.
     * This service implements multiple use cases.
     */
    @Bean
    public LinkService linkService(
            LinkRepositoryPort linkRepository,
            UserRepositoryPort userRepository,
            LinkNotificationPort linkNotificationPort) {
        return new LinkService(linkRepository, userRepository, linkNotificationPort);
    }

    /**
     * Creates the InvitationService singleton.
     */
    @Bean
    public InvitationService invitationService(
            InvitationRepositoryPort invitationRepository,
            UserRepositoryPort userRepository,
            LinkRepositoryPort linkRepository,
            LinkNotificationPort linkNotificationPort,
            FamilyGroupRepositoryPort familyGroupRepository) {
        return new InvitationService(
                invitationRepository,
                userRepository,
                linkRepository,
                linkNotificationPort,
                familyGroupRepository
        );
    }

    /**
     * Creates the AlertService singleton.
     */
    @Bean
    public AlertService alertService(
            AlertRepositoryPort alertRepository,
            LinkRepositoryPort linkRepository) {
        return new AlertService(alertRepository, linkRepository);
    }

    @Bean
    public SmsThreatAlertService smsThreatAlertService(
            SmsThreatAlertRepositoryPort smsThreatAlertRepository,
            LinkRepositoryPort linkRepository) {
        return new SmsThreatAlertService(smsThreatAlertRepository, linkRepository);
    }

    @Bean
    public EmergencyAlertService emergencyAlertService(
            EmergencyAlertRepositoryPort emergencyAlertRepository,
            LinkRepositoryPort linkRepository,
            EmergencyNotificationPort emergencyNotificationPort,
            FamilyGroupRepositoryPort familyGroupRepository) {
        return new EmergencyAlertService(
                emergencyAlertRepository,
                linkRepository,
                emergencyNotificationPort,
                familyGroupRepository
        );
    }

    @Bean
    public EmergencyAudioService emergencyAudioService(
            EmergencyAlertRepositoryPort emergencyAlertRepository,
            EmergencyAudioRepositoryPort emergencyAudioRepository,
            EmergencyAudioStoragePort emergencyAudioStorage) {
        return new EmergencyAudioService(emergencyAlertRepository, emergencyAudioRepository, emergencyAudioStorage);
    }

    @Bean
    public EmergencyAudioHistoryService emergencyAudioHistoryService(
            EmergencyAlertRepositoryPort emergencyAlertRepository,
            EmergencyAudioRepositoryPort emergencyAudioRepository,
            FamilyGroupRepositoryPort familyGroupRepository,
            LinkRepositoryPort linkRepository) {
        return new EmergencyAudioHistoryService(
                emergencyAlertRepository,
                emergencyAudioRepository,
                familyGroupRepository,
                linkRepository
        );
    }

    @Bean
    public EmergencyHistoryService emergencyHistoryService(
            EmergencyAlertRepositoryPort emergencyAlertRepository,
            FamilyGroupRepositoryPort familyGroupRepository,
            LinkRepositoryPort linkRepository) {
        return new EmergencyHistoryService(emergencyAlertRepository, familyGroupRepository, linkRepository);
    }

    @Bean
    public FamilyGroupService familyGroupService(
            FamilyGroupRepositoryPort familyGroupRepository,
            UserRepositoryPort userRepository) {
        return new FamilyGroupService(familyGroupRepository, userRepository);
    }

    @Bean
    public FamilyInvitationService familyInvitationService(
            FamilyInvitationRepositoryPort familyInvitationRepository,
            FamilyGroupRepositoryPort familyGroupRepository,
            UserRepositoryPort userRepository,
            LinkRepositoryPort linkRepository,
            LinkNotificationPort linkNotificationPort) {
        return new FamilyInvitationService(
                familyInvitationRepository,
                familyGroupRepository,
                userRepository,
                linkRepository,
                linkNotificationPort
        );
    }

    @Bean
    public IdentityVerificationService identityVerificationService(
            IdentityVerificationRepositoryPort verificationRepository,
            LinkRepositoryPort linkRepository,
            IdentityVerificationNotificationPort notificationPort) {
        return new IdentityVerificationService(
            verificationRepository,
            linkRepository,
            notificationPort
        );
    }

    @Bean
    public NotificationTokenService notificationTokenService(DeviceTokenRepositoryPort deviceTokenRepository) {
        return new NotificationTokenService(deviceTokenRepository);
    }

    @Bean
    public ThreatAnalysisService threatAnalysisService(SafeBrowsingPort safeBrowsingPort,
                                                       TrustedDomainRepositoryPort trustedDomainRepositoryPort) {
        return new ThreatAnalysisService(safeBrowsingPort, trustedDomainRepositoryPort);
    }

}
