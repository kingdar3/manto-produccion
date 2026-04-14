package com.guardianapp.infrastructure.config;

import com.guardianapp.application.service.AlertService;
import com.guardianapp.application.service.IdentityVerificationService;
import com.guardianapp.application.service.InvitationService;
import com.guardianapp.application.service.NotificationTokenService;
import com.guardianapp.application.service.UserService;
import com.guardianapp.application.service.LinkService;
import com.guardianapp.domain.port.out.DeviceTokenRepositoryPort;
import com.guardianapp.domain.port.out.IdentityVerificationNotificationPort;
import com.guardianapp.domain.port.out.IdentityVerificationRepositoryPort;
import com.guardianapp.domain.port.out.AlertRepositoryPort;
import com.guardianapp.domain.port.out.InvitationRepositoryPort;
import com.guardianapp.domain.port.out.LinkNotificationPort;
import com.guardianapp.domain.port.out.UserRepositoryPort;
import com.guardianapp.domain.port.out.LinkRepositoryPort;
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
            LinkNotificationPort linkNotificationPort) {
        return new InvitationService(invitationRepository, userRepository, linkRepository, linkNotificationPort);
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

}
