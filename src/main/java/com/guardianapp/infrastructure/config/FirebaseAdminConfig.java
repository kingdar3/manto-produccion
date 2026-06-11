package com.guardianapp.infrastructure.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Firebase Admin SDK configuration for server-side FCM.
 */
@Configuration
public class FirebaseAdminConfig {

    private static final Logger log = LoggerFactory.getLogger(FirebaseAdminConfig.class);

    @Bean
    @ConditionalOnProperty(prefix = "firebase", name = "enabled", havingValue = "true")
    public FirebaseMessaging firebaseMessaging(
            @Value("${firebase.service-account-json:}") String serviceAccountJson,
            @Value("${firebase.service-account-path:}") String serviceAccountPath) {
        try (InputStream serviceAccount = openServiceAccountStream(serviceAccountJson, serviceAccountPath)) {
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
                FirebaseApp.initializeApp(options);
                log.info("Firebase Admin initialized successfully");
            }
            return FirebaseMessaging.getInstance();
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to initialize Firebase Admin", ex);
        }
    }

    private InputStream openServiceAccountStream(String serviceAccountJson, String serviceAccountPath) throws IOException {
        if (serviceAccountJson != null && !serviceAccountJson.isBlank()) {
            return new ByteArrayInputStream(serviceAccountJson.getBytes(StandardCharsets.UTF_8));
        }
        if (serviceAccountPath != null && !serviceAccountPath.isBlank()) {
            String trimmedPath = serviceAccountPath.trim();
            if (trimmedPath.startsWith("{")) {
                log.warn("firebase.service-account-path contains inline JSON; use firebase.service-account-json instead");
                return new ByteArrayInputStream(trimmedPath.getBytes(StandardCharsets.UTF_8));
            }
            return new FileInputStream(trimmedPath);
        }
        throw new IllegalStateException("Firebase is enabled but neither firebase.service-account-json nor firebase.service-account-path is configured");
    }
}