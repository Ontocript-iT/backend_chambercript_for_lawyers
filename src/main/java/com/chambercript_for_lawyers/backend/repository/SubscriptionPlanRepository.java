package com.chambercript_for_lawyers.backend.repository;


import com.chambercript_for_lawyers.backend.model.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {
}