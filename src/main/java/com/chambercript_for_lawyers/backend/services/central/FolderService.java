package com.chambercript_for_lawyers.backend.services.central;


import com.chambercript_for_lawyers.backend.dto.request.FolderRequest;
import com.chambercript_for_lawyers.backend.model.Folder;
import org.springframework.http.ResponseEntity;

public interface FolderService {
    ResponseEntity<?> createFolder(FolderRequest folderDTO);
    ResponseEntity<?> getFolderContents(Long folderId);
}