package com.guardianapp.infrastructure.config;

import com.guardianapp.application.service.InvitationService;
import com.guardianapp.application.service.UserService;
import com.guardianapp.application.service.LinkService;
import com.guardianapp.domain.port.in.CreateLinkUseCase;
import com.guardianapp.domain.port.in.GetUserUseCase;
import com.guardianapp.domain.port.in.InvitationUseCase;
import com.guardianapp.domain.port.in.QueryLinksUseCase;
import com.guardianapp.domain.port.in.RegisterUserUseCase;
import com.guardianapp.domain.port.out.InvitationRepositoryPort;
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
            UserRepositoryPort userRepository) {
        return new LinkService(linkRepository, userRepository);
    }

    /**
     * Creates the InvitationService singleton.
     */
    @Bean
    public InvitationService invitationService(
            InvitationRepositoryPort invitationRepository,
            UserRepositoryPort userRepository,
            LinkRepositoryPort linkRepository) {
        return new InvitationService(invitationRepository, userRepository, linkRepository);
    }

    // ==================== Use Case Aliases ====================
    // These beans expose the use cases by their interface types

    @Bean
    public RegisterUserUseCase registerUserUseCase(UserService userService) {
        return userService;
    }

    @Bean
    public GetUserUseCase getUserUseCase(UserService userService) {
        return userService;
    }

    @Bean
    public CreateLinkUseCase createLinkUseCase(LinkService linkService) {
        return linkService;
    }

    @Bean
    public QueryLinksUseCase queryLinksUseCase(LinkService linkService) {
        return linkService;
    }

    @Bean
    public InvitationUseCase invitationUseCase(InvitationService invitationService) {
        return invitationService;
    }
}
