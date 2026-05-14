package com.chambercript_for_lawyers.backend.services.impl;


import com.chambercript_for_lawyers.backend.model.Document;
import com.chambercript_for_lawyers.backend.model.Folder;
import com.chambercript_for_lawyers.backend.repository.DocumentRepository;
import com.chambercript_for_lawyers.backend.repository.FolderRepository;
import com.chambercript_for_lawyers.backend.services.BunnyNetStorageService;
import com.chambercript_for_lawyers.backend.services.central.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;


    private final FolderRepository folderRepository;


    private final BunnyNetStorageService bunnyNetStorageService;


    @Override
    public ResponseEntity<?> uploadDocument(MultipartFile file, String documentType, String version, String uploadedBy, Long folderId,String lawFirmCode) {

        try {
            if (file.isEmpty()) {
                throw new RuntimeException("File is empty");
            }
            Folder folder = folderRepository.findById(folderId)
                    .orElseThrow(() -> new RuntimeException("Folder not found"));

            // 1. Build the logical folder path (e.g., "Client_505/Case_101/01_Pleadings/Medical_Records")
            String logicalFolderPath = buildFolderPath(folder);

            String fileUrl = bunnyNetStorageService.uploadFile(file, logicalFolderPath);

            Document doc = new Document();
            doc.setDocumentName(file.getOriginalFilename());
            doc.setDocumentType(documentType);
            doc.setVersion(version);
            doc.setLawFirmCode(lawFirmCode);
            doc.setFileUrl(fileUrl);
            doc.setUploadedBy(uploadedBy);
            doc.setFolder(folder);

            Document savedDocument = documentRepository.save(doc);

            HashMap<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "Document uploaded successfully");
            response.put("savedDocument", savedDocument);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            HashMap<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 500);
            errorResponse.put("message", "Failed to upload document: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }

    }

    private String buildFolderPath(Folder folder) {
        StringBuilder path = new StringBuilder(folder.getName().replaceAll(" ", "_"));
        Folder current = folder.getParentFolder();

        while (current != null) {
            path.insert(0, current.getName().replaceAll(" ", "_") + "/");
            current = current.getParentFolder();
        }
        return String.format("Client_%d/Case_%d/%s", folder.getClientId(), folder.getCaseId(), path.toString());
    }
}