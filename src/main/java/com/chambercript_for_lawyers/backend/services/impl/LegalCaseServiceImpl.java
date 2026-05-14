package com.chambercript_for_lawyers.backend.services.impl;

import com.chambercript_for_lawyers.backend.dto.request.CaseRegistrationRequest;
import com.chambercript_for_lawyers.backend.dto.request.FolderRequest;
import com.chambercript_for_lawyers.backend.model.CaseType;
import com.chambercript_for_lawyers.backend.model.Court;
import com.chambercript_for_lawyers.backend.model.LegalCase;
import com.chambercript_for_lawyers.backend.repository.CaseTypeRepository;
import com.chambercript_for_lawyers.backend.repository.CourtRepository;
import com.chambercript_for_lawyers.backend.repository.LegalCaseRepository;
import com.chambercript_for_lawyers.backend.services.central.FolderService;
import com.chambercript_for_lawyers.backend.services.central.LegalCaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class LegalCaseServiceImpl implements LegalCaseService {

    private final LegalCaseRepository legalCaseRepository;

    private final CaseTypeRepository caseTypeRepository;
    private final CourtRepository courtRepository;
    private final FolderService folderService;
    @Transactional
    public ResponseEntity<?> registerNewCase(CaseRegistrationRequest dto) {
        try{
            LegalCase newCase = new LegalCase();
            newCase.setCaseNumber(dto.getCaseNumber());
            newCase.setCaseTitle(dto.getCaseTitle());
            newCase.setOppositeParty(dto.getOppositeParty());
            newCase.setFilingDate(dto.getFilingDate());
            newCase.setDescription(dto.getDescription());
            newCase.setLawFirmCode(dto.getLawFirmCode());
            newCase.setAssignedLawyer(dto.getAssignedLawyer());
            newCase.setClientId(dto.getClientId());

            CaseType type = caseTypeRepository.findById(dto.getCaseTypeId())
                    .orElseThrow(() -> new RuntimeException("Case Type not found"));
            Court court = courtRepository.findById(dto.getCourtId())
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
        }catch(Exception e){
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
}