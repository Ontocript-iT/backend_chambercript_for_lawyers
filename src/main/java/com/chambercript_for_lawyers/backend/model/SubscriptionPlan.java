package com.chambercript_for_lawyers.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "subscription_plans")
public class SubscriptionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String planName;
    private Integer maxEmpAccounts;
    private Integer storageGb;
    private Integer maxRecords;
    private String price;
}