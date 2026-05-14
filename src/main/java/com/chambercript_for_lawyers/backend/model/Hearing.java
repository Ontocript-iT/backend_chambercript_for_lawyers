package com.chambercript_for_lawyers.backend.model;

import com.chambercript_for_lawyers.backend.enums.HearingType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "hearings")
public class Hearing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate hearingDate;

    @Enumerated(EnumType.STRING)
    private HearingType hearingType;

    private String notes;
    private LocalDate nextDate;

    // User can enable or disable the reminder
    private boolean smsReminderEnabled = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id")
    @JsonIgnoreProperties({"hearings", "tasks", "hibernateLazyInitializer", "handler"})
    private LegalCase legalCase;

}
