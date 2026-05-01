package com.guardianapp.domain.port.out;

/**
 * Output port for external audio storage.
 */
public interface EmergencyAudioStoragePort {

    UploadResult upload(String fileName, byte[] content, String contentType);

    record UploadResult(String provider, String fileId, String playbackUrl) {
    }
}
