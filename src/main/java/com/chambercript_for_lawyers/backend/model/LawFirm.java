package com.chambercript_for_lawyers.backend.model;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "law_firms")
public class LawFirm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String lawFirmCode;

    private String firmName;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private SubscriptionPlan subscriptionPlan;

    // Usage Tracking Fields
    private Long usedStorageBytes = 0L;
    private Integer usedEmpAccounts = 0;
    private Integer usedRecords = 0;
}