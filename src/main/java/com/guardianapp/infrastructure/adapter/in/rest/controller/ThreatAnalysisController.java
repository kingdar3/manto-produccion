package com.guardianapp.infrastructure.adapter.in.rest.controller;

import com.guardianapp.domain.model.ThreatAnalysisResult;
import com.guardianapp.domain.port.in.AnalyzeThreatUseCase;
import com.guardianapp.domain.port.in.AnalyzeThreatUseCase.AnalyzeThreatCommand;
import com.guardianapp.infrastructure.adapter.in.rest.dto.request.AnalyzeThreatRequest;
import com.guardianapp.infrastructure.adapter.in.rest.dto.response.ThreatAnalysisResponse;
import com.guardianapp.infrastructure.adapter.in.rest.mapper.ThreatAnalysisRestMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoint to analyze URLs against Google Safe Browsing.
 */
@RestController
@RequestMapping("/api/v1/threats")
public class ThreatAnalysisController {

    private final AnalyzeThreatUseCase analyzeThreatUseCase;
    private final ThreatAnalysisRestMapper restMapper;

    public ThreatAnalysisController(AnalyzeThreatUseCase analyzeThreatUseCase,
                                    ThreatAnalysisRestMapper restMapper) {
        this.analyzeThreatUseCase = analyzeThreatUseCase;
        this.restMapper = restMapper;
    }

    /**
     * Analyzes URLs extracted from messages.
     *
     * POST /api/v1/threats/analyze
     */
    @PostMapping("/analyze")
    public ResponseEntity<ThreatAnalysisResponse> analyze(@Valid @RequestBody AnalyzeThreatRequest request) {
        AnalyzeThreatCommand command = new AnalyzeThreatCommand(
            request.message(),
            request.urls(),
            request.sender()
        );

        ThreatAnalysisResult result = analyzeThreatUseCase.analyze(command);
        return ResponseEntity.ok(restMapper.toResponse(result));
    }
}
