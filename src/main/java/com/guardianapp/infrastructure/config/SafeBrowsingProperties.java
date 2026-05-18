package com.guardianapp.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Configuration properties for Google Safe Browsing integration.
 */
@ConfigurationProperties(prefix = "google.safe-browsing")
public class SafeBrowsingProperties {

    private boolean enabled = false;
    private String apiKey;
    private String baseUrl = "https://safebrowsing.googleapis.com";
    private String clientId = "guardian-api";
    private String clientVersion = "1.0.0";
    private List<String> threatTypes = List.of(
        "SOCIAL_ENGINEERING",
        "MALWARE",
        "UNWANTED_SOFTWARE",
        "POTENTIALLY_HARMFUL_APPLICATION"
    );
    private List<String> platformTypes = List.of("ANY_PLATFORM");
    private List<String> threatEntryTypes = List.of("URL");

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }

    public List<String> getThreatTypes() {
        return threatTypes;
    }

    public void setThreatTypes(List<String> threatTypes) {
        this.threatTypes = threatTypes;
    }

    public List<String> getPlatformTypes() {
        return platformTypes;
    }

    public void setPlatformTypes(List<String> platformTypes) {
        this.platformTypes = platformTypes;
    }

    public List<String> getThreatEntryTypes() {
        return threatEntryTypes;
    }

    public void setThreatEntryTypes(List<String> threatEntryTypes) {
        this.threatEntryTypes = threatEntryTypes;
    }
}
