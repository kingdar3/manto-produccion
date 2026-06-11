package com.guardianapp.infrastructure.adapter.out.safebrowsing.dto;

import java.util.List;

/**
 * Request body for Google Safe Browsing threatMatches.find.
 */
public record SafeBrowsingApiRequest(
    ClientInfo client,
    ThreatInfo threatInfo
) {
    public static SafeBrowsingApiRequest fromUrls(
        List<String> urls,
        String clientId,
        String clientVersion,
        List<String> threatTypes,
        List<String> platformTypes,
        List<String> threatEntryTypes
    ) {
        List<ThreatEntry> entries = urls.stream()
            .map(ThreatEntry::new)
            .toList();

        return new SafeBrowsingApiRequest(
            new ClientInfo(clientId, clientVersion),
            new ThreatInfo(threatTypes, platformTypes, threatEntryTypes, entries)
        );
    }

    public record ClientInfo(
        String clientId,
        String clientVersion
    ) {}

    public record ThreatInfo(
        List<String> threatTypes,
        List<String> platformTypes,
        List<String> threatEntryTypes,
        List<ThreatEntry> threatEntries
    ) {}

    public record ThreatEntry(String url) {}
}
