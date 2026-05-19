package com.guardianapp.infrastructure.adapter.in.rest.controller;

import com.guardianapp.domain.model.SmsThreatAlert;
import com.guardianapp.domain.model.valueobject.LinkId;
import com.guardianapp.domain.model.valueobject.SmsThreatAlertId;
import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.domain.port.in.SmsThreatAlertUseCase;
import com.guardianapp.domain.port.in.SmsThreatAlertUseCase.CreateSmsThreatAlertCommand;
import com.guardianapp.domain.port.in.SmsThreatAlertUseCase.ResolveSmsThreatAlertCommand;
import com.guardianapp.infrastructure.adapter.in.rest.dto.request.CreateSmsThreatAlertRequest;
import com.guardianapp.infrastructure.adapter.in.rest.dto.request.ResolveSmsThreatAlertRequest;
import com.guardianapp.infrastructure.adapter.in.rest.dto.response.SmsThreatAlertResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for SMS threat alerts.
 */
@RestController
@RequestMapping("/api/v1/sms-threat-alerts")
public class SmsThreatAlertController {

    private final SmsThreatAlertUseCase smsThreatAlertUseCase;

    public SmsThreatAlertController(SmsThreatAlertUseCase smsThreatAlertUseCase) {
        this.smsThreatAlertUseCase = smsThreatAlertUseCase;
    }

    @PostMapping
    public ResponseEntity<SmsThreatAlertResponse> create(@Valid @RequestBody CreateSmsThreatAlertRequest request) {
        CreateSmsThreatAlertCommand command = new CreateSmsThreatAlertCommand(
            LinkId.of(request.linkId()),
            UserId.of(request.protectedUserId()),
            request.sender(),
            request.messageExcerpt(),
            request.detectedUrl(),
            request.analysisStatus(),
            request.analysisReason()
        );

        SmsThreatAlert created = smsThreatAlertUseCase.create(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(SmsThreatAlertResponse.from(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SmsThreatAlertResponse> getById(@PathVariable String id) {
        return smsThreatAlertUseCase.getById(SmsThreatAlertId.fromString(id))
            .map(alert -> ResponseEntity.ok(SmsThreatAlertResponse.from(alert)))
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pending")
    public ResponseEntity<List<SmsThreatAlertResponse>> getPendingForHost(
            @RequestHeader("X-User-Id") String hostId) {
        List<SmsThreatAlertResponse> response = smsThreatAlertUseCase
            .getPendingForHost(UserId.fromString(hostId))
            .stream()
            .map(SmsThreatAlertResponse::from)
            .toList();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/resolve")
    public ResponseEntity<SmsThreatAlertResponse> resolve(
            @PathVariable String id,
            @Valid @RequestBody ResolveSmsThreatAlertRequest request) {
        ResolveSmsThreatAlertCommand command = new ResolveSmsThreatAlertCommand(
            SmsThreatAlertId.fromString(id),
            UserId.of(request.hostId()),
            request.allowAccess(),
            request.note()
        );
        SmsThreatAlert resolved = smsThreatAlertUseCase.resolve(command);
        return ResponseEntity.ok(SmsThreatAlertResponse.from(resolved));
    }
}
