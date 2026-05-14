package com.chambercript_for_lawyers.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "case_types")
public class CaseType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String typeName; // e.g., Criminal, Civil
    private String description;

    // Self-referencing for sub-categories (e.g., Civil -> Property)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private CaseType parentCategory;
}