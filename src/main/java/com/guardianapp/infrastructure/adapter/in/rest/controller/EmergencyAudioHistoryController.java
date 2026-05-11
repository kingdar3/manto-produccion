package com.guardianapp.infrastructure.adapter.in.rest.controller;

import com.guardianapp.domain.model.valueobject.EmergencyAlertId;
import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.domain.port.in.EmergencyAudioHistoryUseCase;
import com.guardianapp.infrastructure.adapter.in.rest.dto.response.EmergencyAudioRecordingResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for emergency audio history.
 */
@RestController
@RequestMapping("/api/v1/emergencies/{emergencyId}/audio")
public class EmergencyAudioHistoryController {

    private final EmergencyAudioHistoryUseCase emergencyAudioHistoryUseCase;

    public EmergencyAudioHistoryController(EmergencyAudioHistoryUseCase emergencyAudioHistoryUseCase) {
        this.emergencyAudioHistoryUseCase = emergencyAudioHistoryUseCase;
    }

    @GetMapping
    public ResponseEntity<List<EmergencyAudioRecordingResponse>> getAudioHistory(
            @PathVariable String emergencyId,
            @RequestHeader("X-User-Id") String requesterId) {
        List<EmergencyAudioRecordingResponse> response = emergencyAudioHistoryUseCase
                .getByEmergencyId(EmergencyAlertId.fromString(emergencyId), UserId.fromString(requesterId))
                .stream()
                .map(EmergencyAudioRecordingResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }
}
