package com.guardianapp.application.service;

import com.guardianapp.domain.enums.ThreatAnalysisStatus;
import com.guardianapp.domain.enums.ThreatSignal;
import com.guardianapp.domain.enums.UrlThreatStatus;
import com.guardianapp.domain.exception.ThreatAnalysisException;
import com.guardianapp.domain.model.ThreatAnalysisResult;
import com.guardianapp.domain.model.TrustedDomainMatch;
import com.guardianapp.domain.model.UrlThreatAnalysis;
import com.guardianapp.domain.port.in.AnalyzeThreatUseCase;
import com.guardianapp.domain.port.out.SafeBrowsingPort;
import com.guardianapp.domain.port.out.TrustedDomainRepositoryPort;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Application service that orchestrates URL threat analysis.
 */
public class ThreatAnalysisService implements AnalyzeThreatUseCase {

    private static final String SOURCE = "GOOGLE_SAFE_BROWSING";
    private static final String REASON_INVALID_URL = "Invalid URL format. Only HTTP/HTTPS URLs are accepted.";
    private static final String REASON_SAFE = "No threats detected.";
    private static final String REASON_SAFE_WHITELIST = "Trusted domain whitelist match.";
    private static final String REASON_PHISHING = "Detected social engineering (phishing/fraud).";
    private static final String REASON_MALWARE = "Detected malware threat.";
    private static final String REASON_UNWANTED = "Detected unwanted software risk.";
    private static final String REASON_SUSPICIOUS = "Suspicious URL pattern detected.";
    private static final String REASON_ERROR = "Threat analysis failed due to Safe Browsing integration error.";
    private static final Pattern IPV4_PATTERN = Pattern.compile("^\\d{1,3}(\\.\\d{1,3}){3}$");

    private static final Set<String> SUSPICIOUS_TLDS = Set.of(
        "xyz", "top", "click", "gq", "tk", "ml", "cf", "ga", "work", "mom", "zip", "country", "stream", "xin"
    );

    private static final Set<String> SHORTENER_DOMAINS = Set.of(
        "bit.ly", "tinyurl.com", "t.co", "cutt.ly", "shorturl.at", "goo.gl", "is.gd", "ow.ly", "rb.gy"
    );

    private static final Set<String> URGENCY_KEYWORDS = Set.of(
        "urgente", "inmediato", "inmediatamente", "ahora", "hoy", "ultimo aviso", "ultimatum", "evita bloqueo"
    );

    private static final Set<String> CREDENTIAL_KEYWORDS = Set.of(
        "contrasena", "contraseña", "clave", "token", "pin", "codigo de seguridad", "codigo sms", "verificacion"
    );

    private static final Set<String> ACCOUNT_THREAT_KEYWORDS = Set.of(
        "cuenta bloqueada", "cuenta suspendida", "cuenta restringida", "tarjeta bloqueada",
        "actividad sospechosa", "actualiza tus datos", "reactiva tu cuenta"
    );

    private static final Map<String, Set<String>> BRAND_TO_TRUSTED_DOMAINS = Map.of(
        "bcp", Set.of("bcp.com.pe", "viabcp.com"),
        "bbva", Set.of("bbva.pe"),
        "interbank", Set.of("interbank.pe"),
        "scotiabank", Set.of("scotiabank.com.pe"),
        "banbif", Set.of("banbif.com.pe"),
        "pichincha", Set.of("pichincha.pe")
    );

    private final SafeBrowsingPort safeBrowsingPort;
    private final TrustedDomainRepositoryPort trustedDomainRepositoryPort;

    public ThreatAnalysisService(SafeBrowsingPort safeBrowsingPort,
                                 TrustedDomainRepositoryPort trustedDomainRepositoryPort) {
        this.safeBrowsingPort = safeBrowsingPort;
        this.trustedDomainRepositoryPort = trustedDomainRepositoryPort;
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
        Set<String> safeByWhitelist = new HashSet<>();
        Map<String, TrustedDomainMatch> whitelistMatchByUrl = new HashMap<>();
        int invalidCount = 0;

        for (String rawUrl : command.urls()) {
            String normalized = normalize(rawUrl);
            normalizedByOriginal.put(rawUrl, normalized);
            if (isValidHttpUrl(normalized)) {
                String host = extractHost(normalized);
                Optional<TrustedDomainMatch> trustedMatch = trustedDomainRepositoryPort.findMatchForHost(host);
                if (trustedMatch.isPresent()) {
                    safeByWhitelist.add(normalized);
                    whitelistMatchByUrl.put(normalized, trustedMatch.get());
                } else {
                    validUniqueUrls.add(normalized);
                }
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
                results.add(new UrlThreatAnalysis(
                    safeValue(rawUrl),
                    UrlThreatStatus.ERROR,
                    REASON_INVALID_URL,
                    0,
                    List.of(),
                    false,
                    null
                ));
                continue;
            }

            if (safeByWhitelist.contains(normalized)) {
                TrustedDomainMatch match = whitelistMatchByUrl.get(normalized);
                results.add(new UrlThreatAnalysis(
                    normalized,
                    UrlThreatStatus.SAFE,
                    REASON_SAFE_WHITELIST,
                    0,
                    List.of(ThreatSignal.TRUSTED_WHITELIST),
                    true,
                    match.providerName()
                ));
                continue;
            }

            UrlThreatStatus safeBrowsingStatus = threatByUrl.getOrDefault(normalized, UrlThreatStatus.ERROR);
            HeuristicResult heuristic = evaluateHeuristics(command.message(), normalized);

            UrlThreatStatus finalStatus = mergeStatus(safeBrowsingStatus, heuristic.score());
            String finalReason = reasonFor(finalStatus, safeBrowsingStatus, heuristic.score(), heuristic.signals());

            results.add(new UrlThreatAnalysis(
                normalized,
                finalStatus,
                finalReason,
                heuristic.score(),
                heuristic.signals(),
                false,
                null
            ));
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

    private String reasonFor(UrlThreatStatus finalStatus,
                             UrlThreatStatus safeBrowsingStatus,
                             int heuristicScore,
                             List<ThreatSignal> signals) {
        if (safeBrowsingStatus == UrlThreatStatus.PHISHING
            || safeBrowsingStatus == UrlThreatStatus.MALWARE
            || safeBrowsingStatus == UrlThreatStatus.UNWANTED) {
            return reasonFor(safeBrowsingStatus);
        }

        if (finalStatus == UrlThreatStatus.SUSPICIOUS || finalStatus == UrlThreatStatus.PHISHING) {
            return "Heuristic analysis score=" + heuristicScore + ", signals=" + signals;
        }

        return reasonFor(finalStatus);
    }

    private UrlThreatStatus mergeStatus(UrlThreatStatus safeBrowsingStatus, int heuristicScore) {
        if (safeBrowsingStatus == UrlThreatStatus.PHISHING
            || safeBrowsingStatus == UrlThreatStatus.MALWARE
            || safeBrowsingStatus == UrlThreatStatus.UNWANTED
            || safeBrowsingStatus == UrlThreatStatus.ERROR) {
            return safeBrowsingStatus;
        }

        if (heuristicScore >= 70) {
            return UrlThreatStatus.PHISHING;
        }
        if (heuristicScore >= 25) {
            return UrlThreatStatus.SUSPICIOUS;
        }
        return safeBrowsingStatus;
    }

    private HeuristicResult evaluateHeuristics(String message, String url) {
        int score = 0;
        List<ThreatSignal> signals = new ArrayList<>();

        String normalizedMessage = message == null ? "" : message.toLowerCase();
        String host = extractHost(url);

        int urgencyHits = countMatches(normalizedMessage, URGENCY_KEYWORDS);
        if (urgencyHits > 0) {
            signals.add(ThreatSignal.URGENCY_LANGUAGE);
            score += Math.min(25, urgencyHits * 10);
        }

        int credentialHits = countMatches(normalizedMessage, CREDENTIAL_KEYWORDS);
        if (credentialHits > 0) {
            signals.add(ThreatSignal.CREDENTIAL_REQUEST);
            score += Math.min(30, credentialHits * 12);
        }

        int accountThreatHits = countMatches(normalizedMessage, ACCOUNT_THREAT_KEYWORDS);
        if (accountThreatHits > 0) {
            signals.add(ThreatSignal.ACCOUNT_THREAT_LANGUAGE);
            score += Math.min(30, accountThreatHits * 12);
        }

        if (isSuspiciousTld(host)) {
            signals.add(ThreatSignal.SUSPICIOUS_TLD);
            score += 25;
        }
        if (isShortener(host)) {
            signals.add(ThreatSignal.URL_SHORTENER);
            score += 20;
        }
        if (isIpHost(host)) {
            signals.add(ThreatSignal.IP_HOST);
            score += 25;
        }
        if (host.contains("xn--")) {
            signals.add(ThreatSignal.PUNYCODE_DOMAIN);
            score += 30;
        }
        if (host.chars().filter(ch -> ch == '-').count() >= 2) {
            signals.add(ThreatSignal.MANY_HYPHENS);
            score += 10;
        }
        if (host.split("\\.").length > 4) {
            signals.add(ThreatSignal.EXCESSIVE_SUBDOMAINS);
            score += 10;
        }
        if (url.startsWith("http://")) {
            signals.add(ThreatSignal.NON_HTTPS);
            score += 10;
        }
        if (hasBrandDomainMismatch(normalizedMessage, host)) {
            signals.add(ThreatSignal.BRAND_DOMAIN_MISMATCH);
            score += 35;
        }

        return new HeuristicResult(Math.min(score, 100), List.copyOf(signals));
    }

    private int countMatches(String message, Set<String> keywords) {
        int hits = 0;
        for (String keyword : keywords) {
            if (message.contains(keyword)) {
                hits++;
            }
        }
        return hits;
    }

    private boolean isSuspiciousTld(String host) {
        String[] parts = host.split("\\.");
        if (parts.length == 0) {
            return false;
        }
        String tld = parts[parts.length - 1];
        return SUSPICIOUS_TLDS.contains(tld);
    }

    private boolean isShortener(String host) {
        return SHORTENER_DOMAINS.contains(host);
    }

    private boolean isIpHost(String host) {
        return IPV4_PATTERN.matcher(host).matches();
    }

    private boolean hasBrandDomainMismatch(String message, String host) {
        for (Map.Entry<String, Set<String>> entry : BRAND_TO_TRUSTED_DOMAINS.entrySet()) {
            String brand = entry.getKey();
            if (!message.contains(brand)) {
                continue;
            }
            boolean matchesBrandDomain = entry.getValue().stream()
                .anyMatch(domain -> host.equals(domain) || host.endsWith("." + domain));
            if (!matchesBrandDomain) {
                return true;
            }
        }
        return false;
    }

    private String extractHost(String url) {
        try {
            URI uri = new URI(url);
            return uri.getHost() == null ? "" : uri.getHost().toLowerCase();
        } catch (URISyntaxException ex) {
            return "";
        }
    }

    private record HeuristicResult(int score, List<ThreatSignal> signals) {}
}
