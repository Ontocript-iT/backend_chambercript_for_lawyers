package com.chambercript_for_lawyers.backend.services.impl;


import com.chambercript_for_lawyers.backend.dto.request.FolderRequest;
import com.chambercript_for_lawyers.backend.model.Folder;
import com.chambercript_for_lawyers.backend.repository.DocumentRepository;
import com.chambercript_for_lawyers.backend.repository.FolderRepository;
import com.chambercript_for_lawyers.backend.services.central.FolderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FolderServiceImpl implements FolderService {


    private final FolderRepository folderRepository;


    private final DocumentRepository documentRepository;

    @Override
    public ResponseEntity<?> createFolder(FolderRequest dto) {
        try {
            if (dto.getName() == null || dto.getName().trim().isEmpty()) {
                throw new RuntimeException("Folder name is required");
            }
            if (dto.getCaseId() == null) {
                throw new RuntimeException("Case ID is required");
            }
            if (dto.getClientId() == null) {
                throw new RuntimeException("Client ID is required");
            }


            Folder folder = new Folder();
            folder.setName(dto.getName());
            folder.setCaseId(dto.getCaseId());
            folder.setLawFirmCode(dto.getLawFirmCode());
            folder.setClientId(dto.getClientId());

            if (dto.getParentFolderId() != null) {
                Folder parent = folderRepository.findById(dto.getParentFolderId())
                        .orElseThrow(() -> new RuntimeException("Parent folder not found"));
                folder.setParentFolder(parent);
            }

            Folder savedFolder = folderRepository.save(folder);
            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "Folder created successfully");
            response.put("folder", savedFolder);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 500);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @Override
    public ResponseEntity<?> getFolderContents(Long folderId) {
        try {
            Folder folder = folderRepository.findById(folderId)
                    .orElseThrow(() -> new RuntimeException("Folder not found"));
            Map<String, Object> contents = new HashMap<>();
            contents.put("subFolders", folderRepository.findByParentFolderId(folderId));
            contents.put("documents", documentRepository.findByFolderId(folderId));

            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("contents", contents);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 500);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @Override
    public ResponseEntity<?> getFoldersByCaseId(Long caseId) {

        try {
            List<Folder> caseFolder = folderRepository.findByCaseIdAndParentFolderIdIsNull(caseId);
            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("folders", caseFolder);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 500);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @Override
    public ResponseEntity<?> getFoldersByLawFirmCode(String lawFirmCode) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("folders", folderRepository.findByLawFirmCode(lawFirmCode));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 500);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @Override
    public ResponseEntity<?> getFoldersByClientId(Long clientId) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("folders", folderRepository.findByClientId(clientId));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 500);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @Override
    public ResponseEntity<?> renameFolder(Long folderId, String newName) {
        try {
            if (newName == null || newName.trim().isEmpty()) {
                throw new RuntimeException("New folder name is required");
            }
            Folder folder = folderRepository.findById(folderId)
                    .orElseThrow(() -> new RuntimeException("Folder not found"));
            folder.setName(newName);
            Folder updatedFolder = folderRepository.save(folder);
            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "Folder renamed successfully");
            response.put("folder", updatedFolder);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 500);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }

    }

    @Override
    public ResponseEntity<?> deleteFolder(Long folderId) {
        try {
            Folder folder = folderRepository.findById(folderId)
                    .orElseThrow(() -> new RuntimeException("Folder not found"));
            folderRepository.delete(folder);
            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "Folder deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 500);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

}