package com.chambercript_for_lawyers.backend.repository;

import com.chambercript_for_lawyers.backend.model.Court;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourtRepository extends JpaRepository<Court, Long> {
}