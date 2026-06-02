package com.chambercript_for_lawyers.backend.services.impl;

import com.chambercript_for_lawyers.backend.dto.request.PaymentRequest;
import com.chambercript_for_lawyers.backend.model.SubscriptionPayment;
import com.chambercript_for_lawyers.backend.repository.SubscriptionPaymentRepository;
import com.chambercript_for_lawyers.backend.services.central.SubscriptionPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubscriptionPaymentServiceImpl implements SubscriptionPaymentService {

    private final SubscriptionPaymentRepository paymentRepository;

    @Override
    public ResponseEntity<?> recordPayment(String lawFirmCode,PaymentRequest request) {
        Optional<SubscriptionPayment> existingRecord = paymentRepository
                .findByLawFirmCodeAndPaymentYearAndPaymentMonth(
                        lawFirmCode, request.getPaymentYear(), request.getPaymentMonth());

        SubscriptionPayment payment;
        if (existingRecord.isPresent()) {
            payment = existingRecord.get();
        } else {
            payment = new SubscriptionPayment();
            payment.setLawFirmCode(lawFirmCode);
            payment.setPaymentYear(request.getPaymentYear());
            payment.setPaymentMonth(request.getPaymentMonth());
        }

        payment.setIsPaid(true);
        payment.setAmountPaid(request.getAmountPaid());
        payment.setTransactionReference(request.getTransactionReference());
        payment.setPaymentDate(LocalDateTime.now());


    paymentRepository.save(payment);

            return ResponseEntity.ok(Map.of(
                    "status", 200,
                    "message", "Payment recorded successfully",
                    "data", payment
            ));


    }

    public ResponseEntity<?> checkPaymentStatus(String lawFirmCode, int year, int month) {
        Optional<SubscriptionPayment> payment = paymentRepository
                .findByLawFirmCodeAndPaymentYearAndPaymentMonth(lawFirmCode, year, month);

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);

        if (payment.isPresent() && Boolean.TRUE.equals(payment.get().getIsPaid())) {
            response.put("message", "Payment is up to date");
            response.put("data", payment.get());
        } else {
            response.put("message", "Payment is pending for the specified month and year");
            response.put("data", null);
        }

        return ResponseEntity.ok(response);
    }
    @Override
    public ResponseEntity<?> getPaymentHistory(String lawFirmCode) {

        var paymentHistory = paymentRepository.findByLawFirmCodeOrderByPaymentYearDescPaymentMonthDesc(lawFirmCode);

        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "Payment history retrieved successfully",
                "data", paymentHistory
        ));
    }

    @Override
    public ResponseEntity<?> checkCurrentMonthPaymentStatus(String lawFirmCode) {
        LocalDateTime now = LocalDateTime.now();
        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();

        Optional<SubscriptionPayment> payment = paymentRepository
                .findByLawFirmCodeAndPaymentYearAndPaymentMonth(lawFirmCode, currentYear, currentMonth);

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);

        if (payment.isPresent() && Boolean.TRUE.equals(payment.get().getIsPaid())) {
            response.put("message", "Current month payment is up to date");
            response.put("data", payment.get());
        } else {
            response.put("message", "Current month payment is pending");
            response.put("data", null);
        }

        return ResponseEntity.ok(response);
    }

    public boolean existsByLawFirmCodeAndIsPaidTrue(String lawFirmCode) {
        return paymentRepository.existsByLawFirmCodeAndIsPaidTrue(lawFirmCode);

    }
}