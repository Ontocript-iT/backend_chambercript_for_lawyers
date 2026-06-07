package com.chambercript_for_lawyers.backend.services;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class BunnyNetStorageService {

    @Value("${bunny.storage.zone}")
    private String storageZone;

    @Value("${bunny.storage.key}")
    private String accessKey;

    @Value("${bunny.storage.url}")
    private String storageBaseUrl;

    @Value("${bunny.pullzone.url}")
    private String pullZoneUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String uploadFile(MultipartFile file, String folderPath) throws IOException {
        String fileName = file.getOriginalFilename().replaceAll("[^a-zA-Z0-9.-]", "_");

        // Construct the full Bunny.net storage URL: https://storage.bunnycdn.com/zone/path/filename.pdf
        String storageUrl = String.format("%s/%s/%s/%s", storageBaseUrl, storageZone, folderPath, fileName);

        // Set up headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("AccessKey", accessKey);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        // Create the HTTP entity with the file bytes
        HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);

        // Send the PUT request to Bunny.net
        ResponseEntity<String> response = restTemplate.exchange(
                storageUrl,
                HttpMethod.PUT,
                requestEntity,
                String.class
        );

        if (response.getStatusCode() == HttpStatus.CREATED || response.getStatusCode() == HttpStatus.OK) {
            // Return the public URL to view/download the file
            return String.format("%s/%s/%s", pullZoneUrl, folderPath, fileName);
        } else {
            throw new RuntimeException("Failed to upload file to Bunny.net: " + response.getStatusCode());
        }
    }

    public String uploadFile(MultipartFile file, String folderName, String customFileName) throws IOException {
        // Clean up file name (e.g., grab original extension)
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".jpg";

        // Combine custom name with the original extension
        String finalFileName = customFileName + extension;

        // Construct the full Bunny.net storage URL using class properties
        String storageUrl = String.format("%s/%s/%s/%s", storageBaseUrl, storageZone, folderName, finalFileName);

        // Set up headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("AccessKey", accessKey);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        // Create the HTTP entity with the file bytes
        HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);

        // Send the PUT request to Bunny.net
        ResponseEntity<String> response = restTemplate.exchange(
                storageUrl,
                HttpMethod.PUT,
                requestEntity,
                String.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            // Return the public Pull Zone URL so you can save it to the database
            return String.format("%s/%s/%s", pullZoneUrl, folderName, finalFileName);
        } else {
            throw new RuntimeException("Failed to upload custom file to Bunny.net: " + response.getStatusCode());
        }
    }
}