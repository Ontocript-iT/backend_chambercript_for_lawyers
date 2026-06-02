package com.chambercript_for_lawyers.backend.repository;

import com.chambercript_for_lawyers.backend.model.LawFirm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface lawFirmRepository extends JpaRepository<LawFirm, Long> {
    Optional<LawFirm> findByLawFirmCode(String lawFirmCode);
}
