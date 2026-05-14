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
            folder.setClientId(dto.getClientId());

            if (dto.getParentFolderId() != null) {
                Folder parent = folderRepository.findById(dto.getParentFolderId())
                        .orElseThrow(() -> new RuntimeException("Parent folder not found"));
                folder.setParentFolder(parent);
            }

            Folder savedFolder = folderRepository.save(folder);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Folder created successfully");
            response.put("folder", savedFolder);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
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
            response.put("status", "success");
            response.put("contents", contents);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}