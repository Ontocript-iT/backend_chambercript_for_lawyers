package com.chambercript_for_lawyers.backend.repository;


import com.chambercript_for_lawyers.backend.model.SubscriptionPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionPaymentRepository extends JpaRepository<SubscriptionPayment, Long> {

    // Find a specific month's payment for a specific law firm
    Optional<SubscriptionPayment> findByLawFirmCodeAndPaymentYearAndPaymentMonth(
            String lawFirmCode, Integer paymentYear, Integer paymentMonth);

    // Get all payment history for a law firm
    List<SubscriptionPayment> findByLawFirmCodeOrderByPaymentYearDescPaymentMonthDesc(String lawFirmCode);

    boolean existsByLawFirmCodeAndIsPaidTrue(String lawFirmCode);
}