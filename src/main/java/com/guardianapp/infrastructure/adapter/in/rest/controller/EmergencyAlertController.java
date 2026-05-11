package com.guardianapp.infrastructure.adapter.in.rest.controller;

import com.guardianapp.infrastructure.adapter.in.websocket.EmergencyAudioWebSocketHandler;
import com.guardianapp.domain.model.EmergencyAlert;
import com.guardianapp.domain.model.valueobject.EmergencyAlertId;
import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.domain.port.in.EmergencyAlertUseCase;
import com.guardianapp.domain.port.in.EmergencyAlertUseCase.ResolveEmergencyCommand;
import com.guardianapp.domain.port.in.EmergencyAlertUseCase.TriggerEmergencyCommand;
import com.guardianapp.infrastructure.adapter.in.rest.dto.request.ResolveEmergencyAlertRequest;
import com.guardianapp.infrastructure.adapter.in.rest.dto.request.TriggerEmergencyAlertRequest;
import com.guardianapp.infrastructure.adapter.in.rest.dto.response.EmergencyAlertResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;

/**
 * REST controller for emergency alerts.
 */
@RestController
@RequestMapping("/api/v1/emergencies")
public class EmergencyAlertController {

    private final EmergencyAlertUseCase emergencyAlertUseCase;
    private final SimpMessagingTemplate messagingTemplate;
    private final EmergencyAudioWebSocketHandler emergencyAudioWebSocketHandler;

    public EmergencyAlertController(EmergencyAlertUseCase emergencyAlertUseCase,
                                    SimpMessagingTemplate messagingTemplate,
                                    EmergencyAudioWebSocketHandler emergencyAudioWebSocketHandler) {
        this.emergencyAlertUseCase = emergencyAlertUseCase;
        this.messagingTemplate = messagingTemplate;
        this.emergencyAudioWebSocketHandler = emergencyAudioWebSocketHandler;
    }

    @PostMapping
    public ResponseEntity<EmergencyAlertResponse> trigger(@Valid @RequestBody TriggerEmergencyAlertRequest request) {
        TriggerEmergencyCommand command = new TriggerEmergencyCommand(
                com.guardianapp.domain.model.valueobject.LinkId.of(request.linkId()),
                UserId.of(request.protectedUserId()),
                request.latitude(),
                request.longitude()
        );
        EmergencyAlert alert = emergencyAlertUseCase.trigger(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(EmergencyAlertResponse.from(alert));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmergencyAlertResponse> getById(@PathVariable String id) {
        return emergencyAlertUseCase.getById(EmergencyAlertId.fromString(id))
                .map(a -> ResponseEntity.ok(EmergencyAlertResponse.from(a)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/active")
    public ResponseEntity<List<EmergencyAlertResponse>> getActiveForHost(
            @RequestHeader("X-User-Id") String hostId) {
        List<EmergencyAlertResponse> response = emergencyAlertUseCase.getActiveForHost(UserId.fromString(hostId))
                .stream()
                .map(EmergencyAlertResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active/protected")
    public ResponseEntity<List<EmergencyAlertResponse>> getActiveForProtected(
            @RequestHeader("X-User-Id") String protectedUserId) {
        List<EmergencyAlertResponse> response = emergencyAlertUseCase.getActiveForProtected(UserId.fromString(protectedUserId))
                .stream()
                .map(EmergencyAlertResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/resolve")
    public ResponseEntity<EmergencyAlertResponse> resolve(
            @PathVariable String id,
            @Valid @RequestBody ResolveEmergencyAlertRequest request) {
        ResolveEmergencyCommand command = new ResolveEmergencyCommand(
                EmergencyAlertId.fromString(id),
                UserId.of(request.hostId()),
                request.resolutionType(),
                request.note()
        );
        EmergencyAlert alert = emergencyAlertUseCase.resolve(command);
        messagingTemplate.convertAndSend("/topic/emergency/" + alert.getId() + "/status", "RESOLVED");
        emergencyAudioWebSocketHandler.closeSessionsForEmergency(alert.getId().toString());
        return ResponseEntity.ok(EmergencyAlertResponse.from(alert));
    }
}
