package com.chambercript_for_lawyers.backend.services.impl;

import com.chambercript_for_lawyers.backend.dto.request.AuditLogRequest;
import com.chambercript_for_lawyers.backend.dto.request.CaseRegistrationRequest;
import com.chambercript_for_lawyers.backend.dto.request.FolderRequest;
import com.chambercript_for_lawyers.backend.enums.CaseStatus;
import com.chambercript_for_lawyers.backend.model.CaseType;
import com.chambercript_for_lawyers.backend.model.Court;
import com.chambercript_for_lawyers.backend.model.LegalCase;
import com.chambercript_for_lawyers.backend.model.User;
import com.chambercript_for_lawyers.backend.repository.*;
import com.chambercript_for_lawyers.backend.services.central.FolderService;
import com.chambercript_for_lawyers.backend.services.central.LegalCaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LegalCaseServiceImpl implements LegalCaseService {

    private final LegalCaseRepository legalCaseRepository;

    private final CaseTypeRepository caseTypeRepository;
    private final CourtRepository courtRepository;
    private final FolderService folderService;

    private final UserRepository userRepository;

    private final AuditLogServiceImpl auditLogService;

    @Transactional
    public ResponseEntity<?> registerNewCase(CaseRegistrationRequest dto) {
        try {
            LegalCase newCase = new LegalCase();
            newCase.setCaseNumber(dto.getCaseNumber());
            newCase.setCaseTitle(dto.getCaseTitle());
            newCase.setOppositeParty(dto.getOppositeParty());
            newCase.setFilingDate(dto.getFilingDate());
            newCase.setDescription(dto.getDescription());
            newCase.setLawFirmCode(dto.getLawFirmCode());
            newCase.setAssignedLawyer(dto.getAssignedLawyer());
            newCase.setClientId(dto.getClientId());

            CaseType type = caseTypeRepository.findById(dto.getCaseTypeId().getId())
                    .orElseThrow(() -> new RuntimeException("Case Type not found"));
            Court court = courtRepository.findById(dto.getCourtId().getId())
                    .orElseThrow(() -> new RuntimeException("Court not found"));

            newCase.setCaseType(type);
            newCase.setCourt(court);

            LegalCase savedCase = legalCaseRepository.save(newCase);

            generateCaseFolders(savedCase);

            HashMap response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "Case registered successfully");
            response.put("data", savedCase);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            HashMap errorResponse = new HashMap<>();
            errorResponse.put("status", 500);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    private void generateCaseFolders(LegalCase savedCase) {
        String[] defaultFolders = {
                "01_Pleadings_&_Petitions",
                "02_Evidence_&_Exhibits",
                "03_Court_Records"
        };

        for (String folderName : defaultFolders) {
            FolderRequest folderDTO = new FolderRequest();
            folderDTO.setName(folderName);
            folderDTO.setCaseId(savedCase.getId());
            folderDTO.setClientId(savedCase.getClientId());
            folderDTO.setParentFolderId(null);
            folderService.createFolder(folderDTO);
        }
    }

    @Override
    public ResponseEntity<?> getCasesByLawFirmCode(String lawFirmCode) {
        try {
            LegalCase cases = legalCaseRepository.findByLawFirmCode(lawFirmCode);
            HashMap response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "Cases retrieved successfully");
            response.put("data", cases);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            HashMap errorResponse = new HashMap<>();
            errorResponse.put("status", 500);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @Override
    public ResponseEntity<?> getCasesByClientId(Long clientId) {
        try {
            LegalCase cases = legalCaseRepository.findByClientId(clientId);
            HashMap response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "Cases retrieved successfully");
            response.put("data", cases);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            HashMap errorResponse = new HashMap<>();
            errorResponse.put("status", 500);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @Override
    public ResponseEntity<?> getCaseById(Long caseId) {
        try {
            LegalCase legalCase = legalCaseRepository.findById(caseId)
                    .orElseThrow(() -> new RuntimeException("Case not found with ID: " + caseId));
            HashMap response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "Case retrieved successfully");
            response.put("data", legalCase);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            HashMap errorResponse = new HashMap<>();
            errorResponse.put("status", 500);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @Override
    public ResponseEntity<?> getFutureCases(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);

            Page<LegalCase> casePage = legalCaseRepository.findFutureCases(pageable);
            List<LegalCase> cases = casePage.getContent();

            HashMap<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "Future cases retrieved successfully");
            response.put("data", cases);

            // Add Pagination Metadata
            response.put("currentPage", casePage.getNumber());
            response.put("totalItems", casePage.getTotalElements());
            response.put("totalPages", casePage.getTotalPages());
            response.put("caseCount", cases.size()); // Number of items on the current page

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            HashMap<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 500);
            errorResponse.put("message", "Error retrieving future cases: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @Override
    public ResponseEntity<?> updateCase(CaseRegistrationRequest dto, Long caseId) {
        try {
            LegalCase existingCase = legalCaseRepository.findById(caseId)
                    .orElseThrow(() -> new RuntimeException("Case not found with ID: " + caseId));

            existingCase.setCaseNumber(dto.getCaseNumber());
            existingCase.setCaseTitle(dto.getCaseTitle());
            existingCase.setOppositeParty(dto.getOppositeParty());
            existingCase.setFilingDate(dto.getFilingDate());
            existingCase.setDescription(dto.getDescription());
            existingCase.setLawFirmCode(dto.getLawFirmCode());
            existingCase.setCaseType(dto.getCaseTypeId());
            existingCase.setOppositeParty(dto.getOppositeParty());
            existingCase.setCourt(dto.getCourtId());
            existingCase.setAssignedLawyer(dto.getAssignedLawyer());
            existingCase.setClientId(dto.getClientId());

            CaseType type = caseTypeRepository.findById(dto.getCaseTypeId().getId())
                    .orElseThrow(() -> new RuntimeException("Case Type not found"));
            Court court = courtRepository.findById(dto.getCourtId().getId())
                    .orElseThrow(() -> new RuntimeException("Court not found"));

            existingCase.setCaseType(type);
            existingCase.setCourt(court);

            LegalCase updatedCase = legalCaseRepository.save(existingCase);


            AuditLogRequest auditLogRequest = new AuditLogRequest();
            auditLogRequest.setAction("CASE UPDATE");
            auditLogRequest.setActorName(getCurrentUser().getFirstName() + " " + getCurrentUser().getLastName());
            auditLogRequest.setActorId(getCurrentUser().getId());
            auditLogRequest.setDetails("Case updated by user: " + getCurrentUser().getFirstName() + " " + getCurrentUser().getLastName() + " with details: " + updatedCase.toString());
            auditLogRequest.setEntityId(updatedCase.getId().toString());
            auditLogRequest.setEntityName("LegalCase");

            auditLogService.logAction(getCurrentUser().getLawFirmCode(), auditLogRequest);

            HashMap response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "Case updated successfully");
            response.put("data", updatedCase);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            HashMap errorResponse = new HashMap<>();
            errorResponse.put("status", 500);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Optional<User> user = userRepository.findByEmail(authentication.getName());

            return user.orElse(null);
        }
        return null;
    }

    @Override
    public ResponseEntity<?> deleteCase(Long caseId) {
        try {
            LegalCase existingCase = legalCaseRepository.findById(caseId)
                    .orElseThrow(() -> new RuntimeException("Case not found with ID: " + caseId));

            legalCaseRepository.delete(existingCase);

            HashMap response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "Case deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            HashMap errorResponse = new HashMap<>();
            errorResponse.put("status", 500);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @Override
    public ResponseEntity<?> getCaseTypes() {
        try {
            List<CaseType> caseTypes = caseTypeRepository.findAll();
            HashMap response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "Case types retrieved successfully");
            response.put("data", caseTypes);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            HashMap errorResponse = new HashMap<>();
            errorResponse.put("status", 500);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @Override
    public ResponseEntity<?> getAllCourts() {
        try {
            List<Court> courts = courtRepository.findAll();
            HashMap response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "Courts retrieved successfully");
            response.put("data", courts);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            HashMap errorResponse = new HashMap<>();
            errorResponse.put("status", 500);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @Override
    public ResponseEntity<?> updateCaseStatus(Long caseId, CaseStatus status) {
        try {
            LegalCase existingCase = legalCaseRepository.findById(caseId)
                    .orElseThrow(() -> new RuntimeException("Case not found with ID: " + caseId));

            existingCase.setStatus(status);
            LegalCase updatedCase = legalCaseRepository.save(existingCase);

            HashMap response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "Case status updated successfully");
            response.put("data", updatedCase);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            HashMap errorResponse = new HashMap<>();
            errorResponse.put("status", 500);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }

    }

    @Override
    public ResponseEntity<?> getCasesByStatus(CaseStatus status, String lawFirmCode,int page, int size) {

        try {
            Pageable paging = PageRequest.of(page, size);

            Page<LegalCase> pageCases = legalCaseRepository.findByStatusAndLawFirmCode(status, lawFirmCode, paging);

            long pendingCount = legalCaseRepository.countByStatusAndLawFirmCode(CaseStatus.PENDING_REVIEW, lawFirmCode);

            HashMap response = new HashMap<>();
            response.put("status", 200);
            response.put("pendingCount", pendingCount);
            response.put("message", "Cases retrieved successfully");
            response.put("data", pageCases.getContent());

            response.put("currentPage", pageCases.getNumber());
            response.put("totalItems", pageCases.getTotalElements());
            response.put("totalPages", pageCases.getTotalPages());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            HashMap errorResponse = new HashMap<>();
            errorResponse.put("status", 500);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}