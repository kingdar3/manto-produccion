package com.guardianapp.infrastructure.adapter.out.safebrowsing.client;

import com.guardianapp.infrastructure.adapter.out.safebrowsing.dto.SafeBrowsingApiRequest;
import com.guardianapp.infrastructure.adapter.out.safebrowsing.dto.SafeBrowsingApiResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * HTTP client for Google Safe Browsing API.
 */
@Component
public class SafeBrowsingClient {

    private final RestClient safeBrowsingRestClient;

    public SafeBrowsingClient(RestClient safeBrowsingRestClient) {
        this.safeBrowsingRestClient = safeBrowsingRestClient;
    }

    public SafeBrowsingApiResponse findThreatMatches(String apiKey, SafeBrowsingApiRequest request) {
        return safeBrowsingRestClient.post()
            .uri(uriBuilder -> uriBuilder
                .path("/v4/threatMatches:find")
                .queryParam("key", apiKey)
                .build())
            .body(request)
            .retrieve()
            .body(SafeBrowsingApiResponse.class);
    }
}
