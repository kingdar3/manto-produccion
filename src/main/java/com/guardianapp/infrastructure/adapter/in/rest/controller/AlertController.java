package com.guardianapp.infrastructure.adapter.in.rest.controller;

import com.guardianapp.domain.model.Alert;
import com.guardianapp.domain.model.valueobject.AlertId;
import com.guardianapp.domain.model.valueobject.LinkId;
import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.domain.port.in.AlertUseCase;
import com.guardianapp.domain.port.in.AlertUseCase.CreateAlertCommand;
import com.guardianapp.domain.port.in.AlertUseCase.ResolveAlertCommand;
import com.guardianapp.infrastructure.adapter.in.rest.dto.request.CreateAlertRequest;
import com.guardianapp.infrastructure.adapter.in.rest.dto.request.ResolveAlertRequest;
import com.guardianapp.infrastructure.adapter.in.rest.dto.response.AlertResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for alert operations.
 * Handles the phishing alert flow between protected and host users.
 */
@RestController
@RequestMapping("/api/v1/alerts")
public class AlertController {

    private final AlertUseCase alertUseCase;

    public AlertController(AlertUseCase alertUseCase) {
        this.alertUseCase = alertUseCase;
    }

    /**
     * Creates a new alert when a suspicious URL is detected.
     * Called by the protected user's SecureBrowser.
     * 
     * POST /api/v1/alerts
     */
    @PostMapping
    public ResponseEntity<AlertResponse> create(@Valid @RequestBody CreateAlertRequest request) {
        CreateAlertCommand command = new CreateAlertCommand(
            LinkId.of(request.linkId()),
            UserId.of(request.protectedUserId()),
            request.url(),
            request.reason()
        );

        Alert alert = alertUseCase.create(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(AlertResponse.from(alert));
    }

    /**
     * Gets an alert by ID.
     * Used by protected to poll for resolution status.
     * 
     * GET /api/v1/alerts/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<AlertResponse> getById(@PathVariable String id) {
        return alertUseCase.getById(AlertId.fromString(id))
            .map(alert -> ResponseEntity.ok(AlertResponse.from(alert)))
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Gets all pending alerts for the current host user.
     * Used by host dashboard for polling.
     * 
     * GET /api/v1/alerts/pending
     * Header: X-User-Id (Host ID)
     */
    @GetMapping("/pending")
    public ResponseEntity<List<AlertResponse>> getPendingForHost(
            @RequestHeader("X-User-Id") String hostId) {
        
        List<Alert> alerts = alertUseCase.getPendingForHost(UserId.fromString(hostId));
        List<AlertResponse> response = alerts.stream()
            .map(AlertResponse::from)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Gets all alerts for a specific link.
     * 
     * GET /api/v1/alerts/link/{linkId}
     */
    @GetMapping("/link/{linkId}")
    public ResponseEntity<List<AlertResponse>> getByLink(@PathVariable String linkId) {
        List<Alert> alerts = alertUseCase.getByLink(LinkId.fromString(linkId));
        List<AlertResponse> response = alerts.stream()
            .map(AlertResponse::from)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Resolves an alert.
     * The host decides whether to allow (SAFE) or block (BLOCKED) the URL.
     * 
     * PUT /api/v1/alerts/{id}/resolve
     */
    @PutMapping("/{id}/resolve")
    public ResponseEntity<AlertResponse> resolve(
            @PathVariable String id,
            @Valid @RequestBody ResolveAlertRequest request) {

        ResolveAlertCommand command = new ResolveAlertCommand(
            AlertId.fromString(id),
            UserId.of(request.hostId()),
            request.allowAccess(),
            request.note()
        );

        Alert alert = alertUseCase.resolve(command);
        return ResponseEntity.ok(AlertResponse.from(alert));
    }
}
