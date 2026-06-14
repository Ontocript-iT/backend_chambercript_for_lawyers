package com.chambercript_for_lawyers.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subscription_usages")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SubscriptionUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "subscription_id", referencedColumnName = "id")
    private Subscription subscription;

    @Column(nullable = false)
    @Builder.Default
    private Integer currentEmployeesCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private Double usedStorageMb = 0.0; // Track in MB for accuracy

    @Column(nullable = false)
    @Builder.Default
    private Integer usedSmsCount = 0;
}