package com.guardianapp.application.service;

import com.guardianapp.domain.exception.AlertException;
import com.guardianapp.domain.model.Alert;
import com.guardianapp.domain.model.Link;
import com.guardianapp.domain.model.valueobject.AlertId;
import com.guardianapp.domain.model.valueobject.LinkId;
import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.domain.port.in.AlertUseCase;
import com.guardianapp.domain.port.out.AlertRepositoryPort;
import com.guardianapp.domain.port.out.LinkRepositoryPort;

import java.util.List;
import java.util.Optional;

/**
 * Application service that implements alert use cases.
 * Manages the phishing alert flow between protected and host users.
 */
public class AlertService implements AlertUseCase {

    private final AlertRepositoryPort alertRepository;
    private final LinkRepositoryPort linkRepository;

    public AlertService(AlertRepositoryPort alertRepository,
                        LinkRepositoryPort linkRepository) {
        this.alertRepository = alertRepository;
        this.linkRepository = linkRepository;
    }

    @Override
    public Alert create(CreateAlertCommand command) {
        // Validate link exists and is active
        Link link = linkRepository.findById(command.linkId())
            .orElseThrow(() -> AlertException.linkNotFound(command.linkId().toString()));

        if (!link.isActive()) {
            throw AlertException.linkNotActive(command.linkId().toString());
        }

        // Validate the user is the protected in this link
        if (!link.isProtected(command.protectedUserId())) {
            throw AlertException.userNotInLink(
                command.protectedUserId().toString(), 
                command.linkId().toString()
            );
        }

        // Check if a pending alert already exists for this URL
        if (alertRepository.existsPendingByLinkIdAndUrl(command.linkId(), command.suspiciousUrl())) {
            // Return existing pending alert instead of creating duplicate
            return alertRepository.findByLinkIdAndStatus(command.linkId(), 
                    com.guardianapp.domain.enums.AlertStatus.PENDING)
                .stream()
                .filter(a -> a.getSuspiciousUrl().equals(command.suspiciousUrl()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Expected pending alert not found"));
        }

        // Create and save the alert
        Alert alert = Alert.create(
            command.linkId(),
            command.protectedUserId(),
            command.suspiciousUrl(),
            command.reason()
        );

        return alertRepository.save(alert);
    }

    @Override
    public Optional<Alert> getById(AlertId alertId) {
        return alertRepository.findById(alertId);
    }

    @Override
    public List<Alert> getPendingForHost(UserId hostId) {
        return alertRepository.findPendingByHostId(hostId);
    }

    @Override
    public List<Alert> getByLink(LinkId linkId) {
        return alertRepository.findByLinkId(linkId);
    }

    @Override
    public Alert resolve(ResolveAlertCommand command) {
        // Find the alert
        Alert alert = alertRepository.findById(command.alertId())
            .orElseThrow(() -> AlertException.notFound(command.alertId().toString()));

        // Check if already resolved
        if (alert.isResolved()) {
            throw AlertException.alreadyResolved(command.alertId().toString());
        }

        // Validate the user is the host of the link
        Link link = linkRepository.findById(alert.getLinkId())
            .orElseThrow(() -> AlertException.linkNotFound(alert.getLinkId().toString()));

        if (!link.isHost(command.hostId())) {
            throw AlertException.notAuthorizedToResolve(command.hostId().toString());
        }

        // Resolve the alert
        if (command.allowAccess()) {
            alert.resolveAsSafe(command.hostId(), command.note());
        } else {
            alert.resolveAsBlocked(command.hostId(), command.note());
        }

        return alertRepository.save(alert);
    }
}
