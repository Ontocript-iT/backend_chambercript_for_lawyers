package com.chambercript_for_lawyers.backend.model;

import com.chambercript_for_lawyers.backend.enums.CaseStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@Table(name = "cases")
public class LegalCase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String caseNumber;
    private String caseTitle;
    private String oppositeParty;
    private LocalDate filingDate;

    @Enumerated(EnumType.STRING)
    private CaseStatus status = CaseStatus.PENDING_REVIEW; // Default for clerk submissions

    @Column(columnDefinition = "TEXT")
    private String description;

    private String assignedLawyer; // Could also be a ManyToOne relation to a User table
    private Long clientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_type_id")
    private CaseType caseType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "court_id")
    private Court court;

    @OneToMany(mappedBy = "legalCase", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("legalCase")
    private List<Hearing> hearings;

    @OneToMany(mappedBy = "legalCase", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("legalCase")
    private List<Task> tasks;

    private String lawFirmCode;
}
