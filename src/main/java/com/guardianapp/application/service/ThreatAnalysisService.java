package com.guardianapp.application.service;

import com.guardianapp.domain.enums.ThreatAnalysisStatus;
import com.guardianapp.domain.enums.UrlThreatStatus;
import com.guardianapp.domain.exception.ThreatAnalysisException;
import com.guardianapp.domain.model.ThreatAnalysisResult;
import com.guardianapp.domain.model.UrlThreatAnalysis;
import com.guardianapp.domain.port.in.AnalyzeThreatUseCase;
import com.guardianapp.domain.port.out.SafeBrowsingPort;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Application service that orchestrates URL threat analysis.
 */
public class ThreatAnalysisService implements AnalyzeThreatUseCase {

    private static final String SOURCE = "GOOGLE_SAFE_BROWSING";
    private static final String REASON_INVALID_URL = "Invalid URL format. Only HTTP/HTTPS URLs are accepted.";
    private static final String REASON_SAFE = "No threats detected.";
    private static final String REASON_PHISHING = "Detected social engineering (phishing/fraud).";
    private static final String REASON_MALWARE = "Detected malware threat.";
    private static final String REASON_UNWANTED = "Detected unwanted software risk.";
    private static final String REASON_SUSPICIOUS = "Suspicious URL pattern detected.";
    private static final String REASON_ERROR = "Threat analysis failed due to Safe Browsing integration error.";

    private final SafeBrowsingPort safeBrowsingPort;

    public ThreatAnalysisService(SafeBrowsingPort safeBrowsingPort) {
        this.safeBrowsingPort = safeBrowsingPort;
    }

    @Override
    public ThreatAnalysisResult analyze(AnalyzeThreatCommand command) {
        if (command == null) {
            throw ThreatAnalysisException.invalidRequest("Request payload is required");
        }
        if (command.urls() == null || command.urls().isEmpty()) {
            throw ThreatAnalysisException.invalidRequest("At least one URL is required");
        }

        Map<String, String> normalizedByOriginal = new LinkedHashMap<>();
        Set<String> validUniqueUrls = new LinkedHashSet<>();
        int invalidCount = 0;

        for (String rawUrl : command.urls()) {
            String normalized = normalize(rawUrl);
            normalizedByOriginal.put(rawUrl, normalized);
            if (isValidHttpUrl(normalized)) {
                validUniqueUrls.add(normalized);
            } else {
                invalidCount++;
            }
        }

        Map<String, UrlThreatStatus> threatByUrl = new LinkedHashMap<>();
        if (!validUniqueUrls.isEmpty()) {
            try {
                threatByUrl.putAll(safeBrowsingPort.checkUrls(List.copyOf(validUniqueUrls)));
            } catch (RuntimeException ex) {
                for (String validUrl : validUniqueUrls) {
                    threatByUrl.put(validUrl, UrlThreatStatus.ERROR);
                }
            }
        }

        List<UrlThreatAnalysis> results = new ArrayList<>();
        for (String rawUrl : command.urls()) {
            String normalized = normalizedByOriginal.get(rawUrl);
            if (!isValidHttpUrl(normalized)) {
                results.add(new UrlThreatAnalysis(safeValue(rawUrl), UrlThreatStatus.ERROR, REASON_INVALID_URL));
                continue;
            }

            UrlThreatStatus status = threatByUrl.getOrDefault(normalized, UrlThreatStatus.ERROR);
            results.add(new UrlThreatAnalysis(normalized, status, reasonFor(status)));
        }

        ThreatAnalysisStatus globalStatus = resolveGlobalStatus(results);
        int analyzedCount = results.size() - invalidCount;

        return new ThreatAnalysisResult(
            globalStatus,
            List.copyOf(results),
            SOURCE,
            LocalDateTime.now(),
            results.size(),
            analyzedCount,
            invalidCount
        );
    }

    private ThreatAnalysisStatus resolveGlobalStatus(List<UrlThreatAnalysis> results) {
        boolean hasError = results.stream().anyMatch(r -> r.status() == UrlThreatStatus.ERROR);
        boolean hasPhishing = results.stream().anyMatch(r -> r.status() == UrlThreatStatus.PHISHING);
        boolean hasMalware = results.stream().anyMatch(r -> r.status() == UrlThreatStatus.MALWARE);
        boolean hasUnwanted = results.stream().anyMatch(r -> r.status() == UrlThreatStatus.UNWANTED);
        boolean hasSuspicious = results.stream().anyMatch(r -> r.status() == UrlThreatStatus.SUSPICIOUS);
        boolean allError = results.stream().allMatch(r -> r.status() == UrlThreatStatus.ERROR);

        if (allError) {
            return ThreatAnalysisStatus.ANALYSIS_ERROR;
        }

        ThreatAnalysisStatus riskStatus;
        if (hasPhishing) {
            riskStatus = ThreatAnalysisStatus.FRAUD_RISK;
        } else if (hasMalware) {
            riskStatus = ThreatAnalysisStatus.MALWARE_RISK;
        } else if (hasUnwanted) {
            riskStatus = ThreatAnalysisStatus.UNWANTED_RISK;
        } else if (hasSuspicious) {
            riskStatus = ThreatAnalysisStatus.SUSPICIOUS_RISK;
        } else {
            riskStatus = ThreatAnalysisStatus.NO_RISK_DETECTED;
        }

        if (hasError) {
            return ThreatAnalysisStatus.PARTIAL_ANALYSIS;
        }
        return riskStatus;
    }

    private boolean isValidHttpUrl(String url) {
        if (url == null || url.isBlank()) {
            return false;
        }
        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme();
            return ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme))
                && uri.getHost() != null
                && !uri.getHost().isBlank();
        } catch (URISyntaxException ex) {
            return false;
        }
    }

    private String normalize(String url) {
        return url == null ? "" : url.trim();
    }

    private String safeValue(String url) {
        return (url == null || url.isBlank()) ? "(empty)" : url.trim();
    }

    private String reasonFor(UrlThreatStatus status) {
        return switch (status) {
            case SAFE -> REASON_SAFE;
            case PHISHING -> REASON_PHISHING;
            case MALWARE -> REASON_MALWARE;
            case UNWANTED -> REASON_UNWANTED;
            case SUSPICIOUS -> REASON_SUSPICIOUS;
            case ERROR -> REASON_ERROR;
        };
    }
}
