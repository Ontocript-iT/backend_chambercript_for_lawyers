package com.chambercript_for_lawyers.backend.services.central;

import com.chambercript_for_lawyers.backend.dto.request.PaymentRequest;
import org.springframework.http.ResponseEntity;

public interface SubscriptionPaymentService {
    ResponseEntity<?> recordPayment(String lawFirmCode, PaymentRequest request);

    ResponseEntity<?> checkPaymentStatus(String lawFirmCode, int year, int month);

    ResponseEntity<?> getPaymentHistory(String lawFirmCode);

    ResponseEntity<?> checkCurrentMonthPaymentStatus(String lawFirmCode);
}
