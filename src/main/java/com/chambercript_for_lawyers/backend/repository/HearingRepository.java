package com.chambercript_for_lawyers.backend.repository;

import com.chambercript_for_lawyers.backend.enums.ReminderSchedule;
import com.chambercript_for_lawyers.backend.model.Hearing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface HearingRepository extends JpaRepository<Hearing, Long> {

    List<Hearing> findByLegalCaseId(Long caseId);

    List<Hearing> findByHearingDateAndSmsReminderEnabledTrue(LocalDate hearingDate);

    @Query("SELECT h FROM Hearing h JOIN h.reminderSchedules rs " +
            "WHERE h.hearingDate = :date " +
            "AND h.smsReminderEnabled = true " +
            "AND rs = :schedule")
    List<Hearing> findByHearingDateAndSchedule(
            @Param("date") LocalDate date,
            @Param("schedule") ReminderSchedule schedule
    );

    @Query("SELECT h FROM Hearing h JOIN h.customReminderDates crd " +
            "WHERE crd = :today " +
            "AND h.smsReminderEnabled = true")
    List<Hearing> findByCustomReminderDate(@Param("today") LocalDate today);
}