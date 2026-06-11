package com.guardianapp.infrastructure.adapter.out.safebrowsing.adapter;

import com.guardianapp.domain.enums.UrlThreatStatus;
import com.guardianapp.domain.exception.ThreatAnalysisException;
import com.guardianapp.infrastructure.adapter.out.safebrowsing.client.SafeBrowsingClient;
import com.guardianapp.infrastructure.adapter.out.safebrowsing.dto.SafeBrowsingApiRequest;
import com.guardianapp.infrastructure.adapter.out.safebrowsing.dto.SafeBrowsingApiResponse;
import com.guardianapp.infrastructure.adapter.out.safebrowsing.mapper.SafeBrowsingThreatMapper;
import com.guardianapp.infrastructure.config.SafeBrowsingProperties;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SafeBrowsingAdapterTest {

    @Test
    void shouldMapSocialEngineeringAsPhishing() {
        SafeBrowsingProperties properties = buildEnabledProperties();
        SafeBrowsingClient client = new SafeBrowsingClient(null) {
            @Override
            public SafeBrowsingApiResponse findThreatMatches(String apiKey, SafeBrowsingApiRequest request) {
                SafeBrowsingApiResponse.Threat threat = new SafeBrowsingApiResponse.Threat("http://bbva-seguridad.xyz");
                SafeBrowsingApiResponse.ThreatMatch match =
                    new SafeBrowsingApiResponse.ThreatMatch("SOCIAL_ENGINEERING", "ANY_PLATFORM", "URL", threat);
                return new SafeBrowsingApiResponse(List.of(match));
            }
        };

        SafeBrowsingAdapter adapter = new SafeBrowsingAdapter(client, new SafeBrowsingThreatMapper(), properties);
        Map<String, UrlThreatStatus> result = adapter.checkUrls(List.of("http://bbva-seguridad.xyz"));

        assertEquals(UrlThreatStatus.PHISHING, result.get("http://bbva-seguridad.xyz"));
    }

    @Test
    void shouldThrowWhenIntegrationIsDisabled() {
        SafeBrowsingProperties properties = new SafeBrowsingProperties();
        properties.setEnabled(false);

        SafeBrowsingClient client = new SafeBrowsingClient(null);
        SafeBrowsingAdapter adapter = new SafeBrowsingAdapter(client, new SafeBrowsingThreatMapper(), properties);

        assertThrows(ThreatAnalysisException.class, () -> adapter.checkUrls(List.of("https://example.com")));
    }

    private SafeBrowsingProperties buildEnabledProperties() {
        SafeBrowsingProperties properties = new SafeBrowsingProperties();
        properties.setEnabled(true);
        properties.setApiKey("test-api-key");
        return properties;
    }
}
