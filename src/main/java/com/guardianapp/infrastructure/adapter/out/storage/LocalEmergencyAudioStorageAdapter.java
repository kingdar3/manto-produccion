package com.guardianapp.infrastructure.adapter.out.storage;

import com.guardianapp.domain.port.out.EmergencyAudioStoragePort;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Fallback emergency audio storage adapter using local filesystem.
 */
@Component
public class LocalEmergencyAudioStorageAdapter implements EmergencyAudioStoragePort {

    private static final String AUDIO_BASE_DIR = "uploads/emergency-audio";

    @Override
    public UploadResult upload(String fileName, byte[] content, String contentType) {
        try {
            Path baseDir = Path.of(AUDIO_BASE_DIR);
            Files.createDirectories(baseDir);

            Path filePath = baseDir.resolve(fileName);
            Files.write(filePath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            String relativePath = "/" + AUDIO_BASE_DIR + "/" + fileName;
            return new UploadResult("LOCAL", fileName, relativePath);
        } catch (Exception ex) {
            throw new IllegalStateException("Local audio storage failed: " + ex.getMessage(), ex);
        }
    }
}
