package com.chambercript_for_lawyers.backend.repository;

import com.chambercript_for_lawyers.backend.model.CaseType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaseTypeRepository extends JpaRepository<CaseType, Long> {
    // You can add custom queries here later if needed, e.g., finding sub-categories
}