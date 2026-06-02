package com.chambercript_for_lawyers.backend.controller;

import com.chambercript_for_lawyers.backend.dto.request.PaymentRequest;
import com.chambercript_for_lawyers.backend.services.central.SubscriptionPaymentService;
import com.chambercript_for_lawyers.backend.services.central.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/subscriptions/payments")
@RequiredArgsConstructor
public class SubscriptionPaymentController {


    private final SubscriptionPaymentService paymentService;

    private final UserService userService;

    @PostMapping("/record/{lawFirmCode}")
    @PreAuthorize(("hasRole('SUPER_ADMIN')"))
    public ResponseEntity<?> recordPayment(@RequestBody PaymentRequest request,@PathVariable String lawFirmCode) {
        return paymentService.recordPayment(lawFirmCode, request);

    }

    @GetMapping("/status/{lawFirmCode}")
    @PreAuthorize(("hasRole('SUPER_ADMIN')"))
    public ResponseEntity<?> getStatus(
            @RequestParam int year,
            @RequestParam int month,
            @PathVariable String lawFirmCode) {

        return paymentService.checkPaymentStatus(lawFirmCode, year, month);

    }

    @GetMapping("/history/{lawFirmCode}")
    @PreAuthorize(("hasRole('SUPER_ADMIN')"))
    public ResponseEntity<?> getHistory(@PathVariable String lawFirmCode) {
       return paymentService.getPaymentHistory(lawFirmCode);
    }

    @PostMapping("/checkCurrentMonthPaymentStatus/{lawFirmCode}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> checkCurrentMonthPaymentStatus(@PathVariable String lawFirmCode) {
        return paymentService.checkCurrentMonthPaymentStatus(lawFirmCode);
    }
}