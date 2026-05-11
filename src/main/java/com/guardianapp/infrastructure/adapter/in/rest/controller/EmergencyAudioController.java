package com.guardianapp.infrastructure.adapter.in.rest.controller;

import com.guardianapp.domain.model.valueobject.EmergencyAlertId;
import com.guardianapp.domain.model.valueobject.UserId;
import com.guardianapp.domain.port.in.EmergencyAudioUseCase;
import com.guardianapp.domain.port.in.EmergencyAudioUseCase.UploadEmergencyAudioCommand;
import com.guardianapp.infrastructure.adapter.in.rest.dto.response.EmergencyAudioRecordingResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller for emergency audio upload and retrieval.
 */
@RestController
@RequestMapping("/api/v1/emergencies/{emergencyId}/audio")
public class EmergencyAudioController {

    private final EmergencyAudioUseCase emergencyAudioUseCase;

    public EmergencyAudioController(EmergencyAudioUseCase emergencyAudioUseCase) {
        this.emergencyAudioUseCase = emergencyAudioUseCase;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EmergencyAudioRecordingResponse> uploadAudio(
            @PathVariable String emergencyId,
            @RequestHeader("X-User-Id") String protectedUserId,
            @RequestPart("audio") MultipartFile audio,
            @RequestParam(name = "durationSeconds", required = false) Integer durationSeconds) throws Exception {

        UploadEmergencyAudioCommand command = new UploadEmergencyAudioCommand(
                EmergencyAlertId.fromString(emergencyId),
                UserId.fromString(protectedUserId),
                audio.getBytes(),
                audio.getContentType(),
                durationSeconds
        );

        var recording = emergencyAudioUseCase.uploadAudio(command);
        return ResponseEntity.ok(EmergencyAudioRecordingResponse.from(recording));
    }

    @GetMapping("/latest")
    public ResponseEntity<EmergencyAudioRecordingResponse> getLatestAudio(@PathVariable String emergencyId) {
        return emergencyAudioUseCase.getLatestByEmergency(EmergencyAlertId.fromString(emergencyId))
                .map(recording -> ResponseEntity.ok(EmergencyAudioRecordingResponse.from(recording)))
                .orElse(ResponseEntity.notFound().build());
    }
}
