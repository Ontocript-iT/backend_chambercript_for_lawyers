package com.chambercript_for_lawyers.backend.repository;


import com.chambercript_for_lawyers.backend.model.LegalCase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LegalCaseRepository extends JpaRepository<LegalCase, Long> {
    // Example of a custom query you might need later:
    // List<LegalCase> findByAssignedLawyer(String lawyerName);
}