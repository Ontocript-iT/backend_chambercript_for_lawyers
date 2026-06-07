package com.chambercript_for_lawyers.backend.model;

import com.chambercript_for_lawyers.backend.enums.HearingType;
import com.chambercript_for_lawyers.backend.enums.ReminderSchedule;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "hearing_reminder_schedules", joinColumns = @JoinColumn(name = "hearing_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "schedule", length = 50)
    private Set<ReminderSchedule> reminderSchedules = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "hearing_custom_reminders", joinColumns = @JoinColumn(name = "hearing_id"))
    @Column(name = "reminder_date")
    private Set<LocalDate> customReminderDates = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id")
    @JsonIgnoreProperties({"hearings", "tasks", "hibernateLazyInitializer", "handler"})
    private LegalCase legalCase;

    private String lawFirmCode;

}
