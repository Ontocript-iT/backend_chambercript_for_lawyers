package com.chambercript_for_lawyers.backend.repository;

import com.chambercript_for_lawyers.backend.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    Page<AuditLog> findByLawFirmCodeOrderByTimestampDesc(String lawFirmCode, Pageable pageable);
}