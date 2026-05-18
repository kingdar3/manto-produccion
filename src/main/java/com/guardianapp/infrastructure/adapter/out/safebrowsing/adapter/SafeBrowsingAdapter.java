package com.guardianapp.infrastructure.adapter.out.safebrowsing.adapter;

import com.guardianapp.domain.enums.UrlThreatStatus;
import com.guardianapp.domain.exception.ThreatAnalysisException;
import com.guardianapp.domain.port.out.SafeBrowsingPort;
import com.guardianapp.infrastructure.adapter.out.safebrowsing.client.SafeBrowsingClient;
import com.guardianapp.infrastructure.adapter.out.safebrowsing.dto.SafeBrowsingApiRequest;
import com.guardianapp.infrastructure.adapter.out.safebrowsing.dto.SafeBrowsingApiResponse;
import com.guardianapp.infrastructure.adapter.out.safebrowsing.mapper.SafeBrowsingThreatMapper;
import com.guardianapp.infrastructure.config.SafeBrowsingProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Safe Browsing adapter that implements the output port.
 */
@Component
public class SafeBrowsingAdapter implements SafeBrowsingPort {

    private final SafeBrowsingClient client;
    private final SafeBrowsingThreatMapper mapper;
    private final SafeBrowsingProperties properties;

    public SafeBrowsingAdapter(SafeBrowsingClient client,
                               SafeBrowsingThreatMapper mapper,
                               SafeBrowsingProperties properties) {
        this.client = client;
        this.mapper = mapper;
        this.properties = properties;
    }

    @Override
    public Map<String, UrlThreatStatus> checkUrls(List<String> urls) {
        if (urls == null || urls.isEmpty()) {
            return Map.of();
        }

        if (!properties.isEnabled() || properties.getApiKey() == null || properties.getApiKey().isBlank()) {
            throw ThreatAnalysisException.integrationUnavailable();
        }

        Map<String, UrlThreatStatus> result = new LinkedHashMap<>();
        for (String url : urls) {
            result.put(url, UrlThreatStatus.SAFE);
        }

        SafeBrowsingApiRequest request = SafeBrowsingApiRequest.fromUrls(
            urls,
            properties.getClientId(),
            properties.getClientVersion(),
            properties.getThreatTypes(),
            properties.getPlatformTypes(),
            properties.getThreatEntryTypes()
        );

        try {
            SafeBrowsingApiResponse response = client.findThreatMatches(properties.getApiKey(), request);
            if (response == null || response.matches() == null || response.matches().isEmpty()) {
                return result;
            }

            for (SafeBrowsingApiResponse.ThreatMatch match : response.matches()) {
                if (match == null || match.threat() == null || match.threat().url() == null) {
                    continue;
                }
                String url = match.threat().url();
                UrlThreatStatus newStatus = mapper.toStatus(match.threatType());
                UrlThreatStatus currentStatus = result.getOrDefault(url, UrlThreatStatus.SAFE);
                result.put(url, maxSeverity(currentStatus, newStatus));
            }

            return result;
        } catch (RestClientException ex) {
            throw ThreatAnalysisException.integrationError("HTTP request failed", ex);
        } catch (RuntimeException ex) {
            throw ThreatAnalysisException.integrationError("Unexpected Safe Browsing error", ex);
        }
    }

    private UrlThreatStatus maxSeverity(UrlThreatStatus a, UrlThreatStatus b) {
        return severity(a) >= severity(b) ? a : b;
    }

    private int severity(UrlThreatStatus status) {
        return switch (status) {
            case PHISHING -> 5;
            case MALWARE -> 4;
            case UNWANTED -> 3;
            case SUSPICIOUS -> 2;
            case ERROR -> 1;
            case SAFE -> 0;
        };
    }
}
