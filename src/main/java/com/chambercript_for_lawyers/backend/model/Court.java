package com.chambercript_for_lawyers.backend.model;

import com.chambercript_for_lawyers.backend.enums.CourtType;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "courts")
public class Court {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String courtName;

    @Enumerated(EnumType.STRING)
    private CourtType courtType;

    private String location;
}