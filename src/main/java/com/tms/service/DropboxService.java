package com.tms.service;

import com.tms.dto.attachment.external.UploadResponseDataDto;
import com.tms.exception.UploadException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class DropboxService {
    private static final String DROPBOX_API_UPLOAD_URL = "https://content.dropboxapi.com/2/files/upload";
    private static final String DROPBOX_API_DOWNLOAD_URL = "https://content.dropboxapi.com/2/files/download";
    private final RestTemplate restTemplate;
    @Value("${dropbox.token}")
    private String accessToken;

    public UploadResponseDataDto uploadFile(MultipartFile file, String path) {
        try {
            HttpHeaders headers = createHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            byte[] fileBytes = file.getBytes();
            String dropboxApiArg = "{\"path\": \"/TMA/" + path + "/" + file.getOriginalFilename()
                    + "\", \"mode\": \"add\", \"autorename\": true, \"mute\": false}";
            headers.add("Dropbox-API-Arg", dropboxApiArg);
            HttpEntity<byte[]> request = new HttpEntity<>(fileBytes, headers);
            ResponseEntity<UploadResponseDataDto> response = restTemplate.exchange(
                    DROPBOX_API_UPLOAD_URL,
                    HttpMethod.POST,
                    request,
                    UploadResponseDataDto.class
            );
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new UploadException("Can't upload file to Dropbox: " + file);
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception while working with Dropbox API", e);
        }
    }

    public byte[] downloadFile(String dropboxFilePath) {
        try {
            HttpHeaders headers = createHeaders();
            headers.add("Dropbox-API-Arg", "{\"path\": \"/TMA/" + dropboxFilePath + "\"}");
            HttpEntity<Void> request = new HttpEntity<>(headers);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    DROPBOX_API_DOWNLOAD_URL,
                    HttpMethod.POST,
                    request,
                    byte[].class
            );
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new RuntimeException(
                        "Failed to download file from Dropbox: " + dropboxFilePath
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception while downloading file from Dropbox", e);
        }
    }

    public void createFolder(String path) {
        try {
            HttpHeaders headers = createHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String dropboxApiArg = "{\"path\": \"/TMA/" + path + "\", \"autorename\": false}";
            HttpEntity<String> request = new HttpEntity<>(dropboxApiArg, headers);
            ResponseEntity<Void> response = restTemplate.exchange(
                    "https://api.dropboxapi.com/2/files/create_folder_v2",
                    HttpMethod.POST,
                    request,
                    Void.class
            );
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to create folder at path: " + path);
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception while creating folder at path: " + path, e);
        }
    }

    public void deleteFolder(String folderPath) {
        try {
            HttpHeaders headers = createHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String dropboxApiArg = "{\"path\": \"/TMA/" + folderPath + "\"}";
            HttpEntity<String> request = new HttpEntity<>(dropboxApiArg, headers);
            ResponseEntity<Void> response = restTemplate.exchange(
                    "https://api.dropboxapi.com/2/files/delete_v2",
                    HttpMethod.POST,
                    request,
                    Void.class
            );
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to delete folder at path: " + folderPath);
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception while deleting folder at path: " + folderPath, e);
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        return headers;
    }
}
