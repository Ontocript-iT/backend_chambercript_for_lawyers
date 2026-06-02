package com.chambercript_for_lawyers.backend.repository;

import com.chambercript_for_lawyers.backend.model.Hearing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface HearingRepository extends JpaRepository<Hearing, Long> {

    List<Hearing> findByLegalCaseId(Long caseId);

    List<Hearing> findByHearingDateAndSmsReminderEnabledTrue(LocalDate hearingDate);
}