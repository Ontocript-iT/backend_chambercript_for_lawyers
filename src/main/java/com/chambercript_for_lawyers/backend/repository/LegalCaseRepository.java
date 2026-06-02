package com.chambercript_for_lawyers.backend.repository;


import com.chambercript_for_lawyers.backend.model.LegalCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LegalCaseRepository extends JpaRepository<LegalCase, Long> {
    LegalCase findByLawFirmCode(String lawFirmCode);

    LegalCase findByClientId(Long clientId);

    @Query("SELECT DISTINCT c FROM LegalCase c JOIN c.hearings h WHERE h.hearingDate >= CURRENT_DATE")
    List<LegalCase> findFutureCases();
    // Example of a custom query you might need later:
    // List<LegalCase> findByAssignedLawyer(String lawyerName);
}