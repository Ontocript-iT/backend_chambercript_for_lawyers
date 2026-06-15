package com.chambercript_for_lawyers.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "law_firm_code", length = 50)
    private String lawFirmCode;

    @Column(nullable = false, length = 50)
    private String action;

    @Column(nullable = false, length = 50)
    private String entityName;

    @Column(length = 50)
    private String entityId;

    @Column(columnDefinition = "TEXT")
    private String details;

    private String actorName;

    private Long actorId;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime timestamp;
}