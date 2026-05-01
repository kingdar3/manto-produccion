package com.guardianapp.infrastructure.adapter.out.storage;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.guardianapp.domain.port.out.EmergencyAudioStoragePort;
import com.guardianapp.infrastructure.config.GoogleDriveProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Emergency audio storage adapter backed by Google Drive.
 */
@Component
@Primary
@ConditionalOnBean(Drive.class)
public class GoogleDriveEmergencyAudioStorageAdapter implements EmergencyAudioStoragePort {

    private static final Logger log = LoggerFactory.getLogger(GoogleDriveEmergencyAudioStorageAdapter.class);

    private final Drive drive;
    private final GoogleDriveProperties properties;

    public GoogleDriveEmergencyAudioStorageAdapter(Drive drive, GoogleDriveProperties properties) {
        this.drive = drive;
        this.properties = properties;
    }

    @Override
    public UploadResult upload(String fileName, byte[] content, String contentType) {
        try {
            File fileMetadata = new File();
            fileMetadata.setName(fileName);
            fileMetadata.setParents(List.of(properties.getFolderId()));

            ByteArrayContent mediaContent = new ByteArrayContent(contentType, content);
            File uploaded = drive.files().create(fileMetadata, mediaContent)
                    .setFields("id,webViewLink")
                    .execute();

            return new UploadResult("GOOGLE_DRIVE", uploaded.getId(), uploaded.getWebViewLink());
        } catch (Exception ex) {
            log.error("Failed uploading emergency audio to Google Drive", ex);
            throw new IllegalStateException("Google Drive upload failed: " + ex.getMessage(), ex);
        }
    }
}
