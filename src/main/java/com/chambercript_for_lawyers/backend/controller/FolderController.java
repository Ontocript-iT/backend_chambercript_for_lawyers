package com.chambercript_for_lawyers.backend.controller;

import com.chambercript_for_lawyers.backend.dto.request.FolderRequest;
import com.chambercript_for_lawyers.backend.services.central.FolderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/folders")
@RequiredArgsConstructor
public class FolderController {

    private final FolderService folderService;

    @PostMapping("/create")
    public ResponseEntity<?> createFolder(@RequestBody FolderRequest folderDTO) {
            return folderService.createFolder(folderDTO);
    }

    @GetMapping("/{id}/contents")
    public ResponseEntity<?> getFolderContents(@PathVariable Long id) {
        return folderService.getFolderContents(id);
    }
}