package com.chambercript_for_lawyers.backend.dto.request;


import lombok.Data;

@Data
public class PaymentRequest {
    private Integer paymentYear;
    private Integer paymentMonth;
    private Double amountPaid;
    private String transactionReference;
}