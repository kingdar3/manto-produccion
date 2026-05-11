package com.guardianapp.infrastructure.adapter.in.rest.controller;

import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.domain.port.in.EmergencyHistoryUseCase;
import com.guardianapp.infrastructure.adapter.in.rest.dto.response.EmergencyAlertResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for emergency history.
 */
@RestController
@RequestMapping("/api/v1/emergencies/history")
public class EmergencyHistoryController {

    private final EmergencyHistoryUseCase emergencyHistoryUseCase;

    public EmergencyHistoryController(EmergencyHistoryUseCase emergencyHistoryUseCase) {
        this.emergencyHistoryUseCase = emergencyHistoryUseCase;
    }

    @GetMapping
    public ResponseEntity<List<EmergencyAlertResponse>> getHistoryForHost(
            @RequestHeader("X-User-Id") String hostId) {
        List<EmergencyAlertResponse> response = emergencyHistoryUseCase.getHistoryForHost(UserId.fromString(hostId))
                .stream()
                .map(EmergencyAlertResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }
}
