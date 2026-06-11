package com.guardianapp.application.service;

import com.guardianapp.domain.enums.ThreatAnalysisStatus;
import com.guardianapp.domain.enums.UrlThreatStatus;
import com.guardianapp.domain.model.ThreatAnalysisResult;
import com.guardianapp.domain.port.in.AnalyzeThreatUseCase.AnalyzeThreatCommand;
import com.guardianapp.domain.port.out.SafeBrowsingPort;
import com.guardianapp.domain.port.out.TrustedDomainRepositoryPort;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ThreatAnalysisServiceTest {

    private static final TrustedDomainRepositoryPort NO_WHITELIST = host -> Optional.empty();

    @Test
    void shouldReturnFraudRiskWhenPhishingDetected() {
        SafeBrowsingPort port = urls -> {
            Map<String, UrlThreatStatus> response = new HashMap<>();
            response.put("http://bbva-seguridad.xyz", UrlThreatStatus.PHISHING);
            return response;
        };

        ThreatAnalysisService service = new ThreatAnalysisService(port, NO_WHITELIST);
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

        ThreatAnalysisService service = new ThreatAnalysisService(port, NO_WHITELIST);
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

        ThreatAnalysisService service = new ThreatAnalysisService(port, NO_WHITELIST);
        ThreatAnalysisResult result = service.analyze(new AnalyzeThreatCommand(
            "Mensaje",
            List.of("https://example.com", "not-a-url"),
            "+51999999999"
        ));

        assertEquals(ThreatAnalysisStatus.ANALYSIS_ERROR, result.status());
        assertEquals(2, result.urlResults().size());
    }

    @Test
    void shouldMarkWhitelistedDomainAsSafe() {
        SafeBrowsingPort port = urls -> Map.of("https://www.facebook.com/login", UrlThreatStatus.PHISHING);
        TrustedDomainRepositoryPort whitelist = host -> {
            if (host.endsWith("facebook.com")) {
                return Optional.of(new com.guardianapp.domain.model.TrustedDomainMatch(
                    "facebook.com", "Facebook", "SOCIAL"
                ));
            }
            return Optional.empty();
        };

        ThreatAnalysisService service = new ThreatAnalysisService(port, whitelist);
        ThreatAnalysisResult result = service.analyze(new AnalyzeThreatCommand(
            "Revisa este enlace",
            List.of("https://www.facebook.com/login"),
            "+51999999999"
        ));

        assertEquals(ThreatAnalysisStatus.NO_RISK_DETECTED, result.status());
        assertEquals(UrlThreatStatus.SAFE, result.urlResults().getFirst().status());
        assertEquals(true, result.urlResults().getFirst().whitelisted());
    }
}
