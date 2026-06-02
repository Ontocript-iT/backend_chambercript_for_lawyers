package com.chambercript_for_lawyers.backend.controller;

import com.chambercript_for_lawyers.backend.dto.request.FolderRequest;
import com.chambercript_for_lawyers.backend.services.central.FolderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/folders")
@RequiredArgsConstructor
public class FolderController {

    private final FolderService folderService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'JUNIOR_LAWYER')")
    public ResponseEntity<?> createFolder(@RequestBody FolderRequest folderDTO) {
            return folderService.createFolder(folderDTO);
    }

    @GetMapping("/{id}/contents")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'JUNIOR_LAWYER')")
    public ResponseEntity<?> getFolderContents(@PathVariable Long id) {
        return folderService.getFolderContents(id);
    }

    @GetMapping("/getFoldersByCaseId/{caseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'JUNIOR_LAWYER')")
    public ResponseEntity<?> getFoldersByCaseId(@PathVariable Long caseId) {
        return folderService.getFoldersByCaseId(caseId);
    }

    @GetMapping("/getFoldersByLawFirmCode/{lawFirmCode}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'JUNIOR_LAWYER')")
    public ResponseEntity<?> getFoldersByLawFirmCode(@PathVariable String lawFirmCode) {
        return folderService.getFoldersByLawFirmCode(lawFirmCode);
    }

    @GetMapping("/getFolderByClientId/{clientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'JUNIOR_LAWYER')")
    public ResponseEntity<?> getFolderByClientId(@PathVariable Long clientId) {
        return folderService.getFoldersByClientId(clientId);
    }

    @PutMapping("/renameFolder/{folderId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'JUNIOR_LAWYER')")
    public ResponseEntity<?> renameFolder(@PathVariable Long folderId, @RequestParam String newName) {
        return folderService.renameFolder(folderId, newName);
    }

    @DeleteMapping("/deleteFolder/{folderId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'JUNIOR_LAWYER')")
    public ResponseEntity<?> deleteFolder(@PathVariable Long folderId) {
        return folderService.deleteFolder(folderId);
    }
}