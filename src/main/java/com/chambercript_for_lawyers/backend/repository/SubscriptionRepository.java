package com.chambercript_for_lawyers.backend.repository;

import com.chambercript_for_lawyers.backend.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByAdminId(Long adminId);
}
