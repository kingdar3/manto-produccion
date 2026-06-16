package com.guardianapp.application.service;

import com.guardianapp.domain.enums.SmsThreatAlertStatus;
import com.guardianapp.domain.enums.UrlThreatStatus;
import com.guardianapp.domain.exception.SmsThreatAlertException;
import com.guardianapp.domain.model.Link;
import com.guardianapp.domain.model.SmsThreatAlert;
import com.guardianapp.domain.model.valueobject.SmsThreatAlertId;
import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.domain.port.in.SmsThreatAlertUseCase;
import com.guardianapp.domain.port.out.LinkRepositoryPort;
import com.guardianapp.domain.port.out.SmsThreatAlertRepositoryPort;

import java.util.List;
import java.util.Optional;

/**
 * Application service for SMS threat alerts.
 */
public class SmsThreatAlertService implements SmsThreatAlertUseCase {

    private final SmsThreatAlertRepositoryPort smsThreatAlertRepository;
    private final LinkRepositoryPort linkRepository;

    public SmsThreatAlertService(SmsThreatAlertRepositoryPort smsThreatAlertRepository,
                                 LinkRepositoryPort linkRepository) {
        this.smsThreatAlertRepository = smsThreatAlertRepository;
        this.linkRepository = linkRepository;
    }

    @Override
    public SmsThreatAlert create(CreateSmsThreatAlertCommand command) {
        Link link = linkRepository.findById(command.linkId())
            .orElseThrow(() -> SmsThreatAlertException.linkNotFound(command.linkId().toString()));

        if (!link.isActive()) {
            throw SmsThreatAlertException.linkNotActive(command.linkId().toString());
        }

        if (!link.isProtected(command.protectedUserId())) {
            throw SmsThreatAlertException.userNotInLink(
                command.protectedUserId().toString(),
                command.linkId().toString()
            );
        }

        if (requiresHostReview(command.analysisStatus())
                && command.detectedUrl() != null
                && !command.detectedUrl().isBlank()
                && smsThreatAlertRepository.existsPendingByLinkIdAndUrl(command.linkId(), command.detectedUrl())) {
            return smsThreatAlertRepository.findByLinkIdAndStatus(command.linkId(), SmsThreatAlertStatus.PENDING)
                .stream()
                .filter(a -> command.detectedUrl().equals(a.getDetectedUrl()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Expected pending SMS threat alert not found"));
        }

        SmsThreatAlert alert = SmsThreatAlert.create(
            command.linkId(),
            command.protectedUserId(),
            link.getHostId(),
            command.sender(),
            command.messageExcerpt(),
            command.detectedUrl(),
            command.analysisStatus(),
            command.analysisReason()
        );

        return smsThreatAlertRepository.save(alert);
    }

    @Override
    public Optional<SmsThreatAlert> getById(SmsThreatAlertId alertId) {
        return smsThreatAlertRepository.findById(alertId);
    }

    @Override
    public List<SmsThreatAlert> getPendingForHost(UserId hostId) {
        return smsThreatAlertRepository.findPendingByHostId(hostId);
    }

    @Override
    public List<SmsThreatAlert> getHistoryForHost(UserId hostId) {
        return smsThreatAlertRepository.findHistoryByHostId(hostId);
    }

    @Override
    public SmsThreatAlert resolve(ResolveSmsThreatAlertCommand command) {
        SmsThreatAlert alert = smsThreatAlertRepository.findById(command.alertId())
            .orElseThrow(() -> SmsThreatAlertException.notFound(command.alertId().toString()));

        if (alert.isResolved()) {
            throw SmsThreatAlertException.alreadyResolved(command.alertId().toString());
        }

        if (!alert.getHostUserId().equals(command.hostId())) {
            throw SmsThreatAlertException.notAuthorizedToResolve(command.hostId().toString());
        }

        if (command.allowAccess()) {
            alert.resolveAsSafe(command.hostId(), command.note());
        } else {
            alert.resolveAsBlocked(command.hostId(), command.note());
        }

        return smsThreatAlertRepository.save(alert);
    }

    private boolean requiresHostReview(UrlThreatStatus status) {
        return status == UrlThreatStatus.PHISHING
                || status == UrlThreatStatus.MALWARE
                || status == UrlThreatStatus.UNWANTED
                || status == UrlThreatStatus.SUSPICIOUS
                || status == UrlThreatStatus.ERROR;
    }
}
