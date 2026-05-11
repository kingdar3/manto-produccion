package com.guardianapp.application.service;

import com.guardianapp.domain.exception.IdentityVerificationException;
import com.guardianapp.domain.model.IdentityVerification;
import com.guardianapp.domain.model.Link;
import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.domain.model.valueobject.VerificationId;
import com.guardianapp.domain.port.in.IdentityVerificationUseCase;
import com.guardianapp.domain.port.out.IdentityVerificationNotificationPort;
import com.guardianapp.domain.port.out.IdentityVerificationRepositoryPort;
import com.guardianapp.domain.port.out.LinkRepositoryPort;

import java.util.List;
import java.util.Optional;

/**
 * Application service for identity verification flow.
 */
public class IdentityVerificationService implements IdentityVerificationUseCase {

    private final IdentityVerificationRepositoryPort verificationRepository;
    private final LinkRepositoryPort linkRepository;
    private final IdentityVerificationNotificationPort notificationPort;

    public IdentityVerificationService(
            IdentityVerificationRepositoryPort verificationRepository,
            LinkRepositoryPort linkRepository,
            IdentityVerificationNotificationPort notificationPort) {
        this.verificationRepository = verificationRepository;
        this.linkRepository = linkRepository;
        this.notificationPort = notificationPort;
    }

    @Override
    public IdentityVerification create(CreateVerificationCommand command) {
        Link link = linkRepository.findById(command.linkId())
            .orElseThrow(() -> IdentityVerificationException.linkNotFound(command.linkId().toString()));

        if (!link.isActive()) {
            throw IdentityVerificationException.linkNotActive(command.linkId().toString());
        }

        if (!link.isProtected(command.protectedUserId())) {
            throw IdentityVerificationException.userNotAuthorized(command.protectedUserId().toString());
        }

        IdentityVerification verification = IdentityVerification.create(
            command.linkId(),
            command.protectedUserId(),
            link.getHostId(),
            command.claimedPerson()
        );

        IdentityVerification saved = verificationRepository.save(verification);
        notificationPort.notifyCreated(saved);
        return saved;
    }

    @Override
    public IdentityVerification respond(RespondVerificationCommand command) {
        IdentityVerification verification = verificationRepository.findById(command.verificationId())
            .orElseThrow(() -> IdentityVerificationException.notFound(command.verificationId().toString()));

        if (verification.isExpired()) {
            verification.expireIfNeeded();
            verificationRepository.save(verification);
            throw IdentityVerificationException.expired(command.verificationId().toString());
        }

        if (!verification.isPending()) {
            throw IdentityVerificationException.alreadyResolved(command.verificationId().toString());
        }

        if (command.approved()) {
            verification.approve(command.hostUserId(), command.note());
        } else {
            verification.reject(command.hostUserId(), command.note());
        }

        IdentityVerification saved = verificationRepository.save(verification);
        notificationPort.notifyResolved(saved);
        return saved;
    }

    @Override
    public Optional<IdentityVerification> getById(VerificationId verificationId) {
        Optional<IdentityVerification> verificationOpt = verificationRepository.findById(verificationId);
        verificationOpt.ifPresent(v -> {
            if (v.isPending() && v.isExpired()) {
                v.expireIfNeeded();
                verificationRepository.save(v);
            }
        });
        return verificationOpt;
    }

    @Override
    public List<IdentityVerification> getPendingByHost(UserId hostId) {
        List<IdentityVerification> pending = verificationRepository.findPendingByHost(hostId);
        pending.forEach(v -> {
            if (v.isExpired()) {
                v.expireIfNeeded();
                verificationRepository.save(v);
            }
        });
        return verificationRepository.findPendingByHost(hostId);
    }
}
