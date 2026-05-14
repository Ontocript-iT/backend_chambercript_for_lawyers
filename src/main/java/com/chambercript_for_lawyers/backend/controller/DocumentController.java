package com.chambercript_for_lawyers.backend.controller;

import com.chambercript_for_lawyers.backend.services.central.DocumentService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    public ResponseEntity<?> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentType") String documentType,
            @RequestParam("version") String version,
            @RequestParam("uploadedBy") String uploadedBy,
            @RequestParam("folderId") Long folderId,
            @RequestParam("lawFirmCode") String lawFirmCode){
            return documentService.uploadDocument(file, documentType, version, uploadedBy, folderId,lawFirmCode);

    }
}