package com.guardianapp.infrastructure.config;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

/**
 * Google Drive client configuration.
 */
@Configuration
@EnableConfigurationProperties(GoogleDriveProperties.class)
public class GoogleDriveConfig {

    @Bean
    @ConditionalOnProperty(prefix = "google.drive", name = "enabled", havingValue = "true")
    public Drive googleDriveClient(GoogleDriveProperties properties)
            throws IOException, GeneralSecurityException {
        GoogleCredentials credentials = GoogleCredentials
                .fromStream(new FileInputStream(properties.getServiceAccountPath()))
                .createScoped(List.of("https://www.googleapis.com/auth/drive.file"));

        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);
        return new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName("guardian-api")
                .build();
    }
}
