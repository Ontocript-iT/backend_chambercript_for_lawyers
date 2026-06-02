package com.chambercript_for_lawyers.backend.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "subscription_payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String lawFirmCode;

    @Column(nullable = false)
    private Integer paymentYear; // e.g., 2026

    @Column(nullable = false)
    private Integer paymentMonth; // e.g., 5 for May

    @Column(nullable = false)
    private Boolean isPaid = false;

    private Double amountPaid;

    private LocalDateTime paymentDate;

    private String transactionReference;
}