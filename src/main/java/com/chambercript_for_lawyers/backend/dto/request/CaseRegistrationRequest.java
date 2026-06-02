package com.chambercript_for_lawyers.backend.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CaseRegistrationRequest {
    private String caseNumber;
    private String caseTitle;
    private String oppositeParty;
    private LocalDate filingDate;
    private String description;
    private String assignedLawyer;

    private Long clientId;
    private Long caseTypeId;
    private Long courtId;
    private String lawFirmCode;
}
