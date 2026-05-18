package com.guardianapp.application.service;

import com.guardianapp.domain.enums.ThreatAnalysisStatus;
import com.guardianapp.domain.enums.UrlThreatStatus;
import com.guardianapp.domain.model.ThreatAnalysisResult;
import com.guardianapp.domain.port.in.AnalyzeThreatUseCase.AnalyzeThreatCommand;
import com.guardianapp.domain.port.out.SafeBrowsingPort;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ThreatAnalysisServiceTest {

    @Test
    void shouldReturnFraudRiskWhenPhishingDetected() {
        SafeBrowsingPort port = urls -> {
            Map<String, UrlThreatStatus> response = new HashMap<>();
            response.put("http://bbva-seguridad.xyz", UrlThreatStatus.PHISHING);
            return response;
        };

        ThreatAnalysisService service = new ThreatAnalysisService(port);
        ThreatAnalysisResult result = service.analyze(new AnalyzeThreatCommand(
            "BBVA: su cuenta fue bloqueada",
            List.of("http://bbva-seguridad.xyz"),
            "+51999999999"
        ));

        assertEquals(ThreatAnalysisStatus.FRAUD_RISK, result.status());
        assertEquals(UrlThreatStatus.PHISHING, result.urlResults().getFirst().status());
    }

    @Test
    void shouldReturnNoRiskDetectedWhenAllUrlsAreSafe() {
        SafeBrowsingPort port = urls -> {
            Map<String, UrlThreatStatus> response = new HashMap<>();
            response.put("https://example.com", UrlThreatStatus.SAFE);
            return response;
        };

        ThreatAnalysisService service = new ThreatAnalysisService(port);
        ThreatAnalysisResult result = service.analyze(new AnalyzeThreatCommand(
            "Mensaje normal",
            List.of("https://example.com"),
            "+51999999999"
        ));

        assertEquals(ThreatAnalysisStatus.NO_RISK_DETECTED, result.status());
        assertEquals(UrlThreatStatus.SAFE, result.urlResults().getFirst().status());
    }

    @Test
    void shouldReturnAnalysisErrorWhenIntegrationFailsAndNoUrlCanBeAnalyzed() {
        SafeBrowsingPort port = urls -> {
            throw new RuntimeException("Safe Browsing down");
        };

        ThreatAnalysisService service = new ThreatAnalysisService(port);
        ThreatAnalysisResult result = service.analyze(new AnalyzeThreatCommand(
            "Mensaje",
            List.of("https://example.com", "not-a-url"),
            "+51999999999"
        ));

        assertEquals(ThreatAnalysisStatus.ANALYSIS_ERROR, result.status());
        assertEquals(2, result.urlResults().size());
    }
}
