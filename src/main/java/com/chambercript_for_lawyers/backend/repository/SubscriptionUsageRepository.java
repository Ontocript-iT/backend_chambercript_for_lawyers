package com.chambercript_for_lawyers.backend.repository;

import com.chambercript_for_lawyers.backend.model.SubscriptionUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface SubscriptionUsageRepository extends JpaRepository<SubscriptionUsage, Long> {

    // Find usage by the admin's subscription
    SubscriptionUsage findBySubscriptionAdminId(Long adminId);

    // Atomic update to prevent race conditions during concurrent uploads
    @Modifying
    @Query("UPDATE SubscriptionUsage u SET u.usedStorageMb = u.usedStorageMb + :fileSizeMb WHERE u.subscription.id = :subscriptionId")
    void incrementStorageUsage(Long subscriptionId, Double fileSizeMb);

    @Modifying
    @Query("UPDATE SubscriptionUsage u SET u.currentEmployeesCount = u.currentEmployeesCount + 1 WHERE u.subscription.id = :subscriptionId")
    void incrementEmployeeCount(Long subscriptionId);

    @Modifying
    @Transactional
    @Query("UPDATE SubscriptionUsage s SET s.usedStorageMb = s.usedStorageMb + :amount WHERE s.id = :id")
    void incrementStorageUsed(@Param("id") Long id, @Param("amount") double amount);
}