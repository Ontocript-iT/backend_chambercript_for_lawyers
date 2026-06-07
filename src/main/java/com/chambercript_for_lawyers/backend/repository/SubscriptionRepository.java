package com.chambercript_for_lawyers.backend.repository;

import com.chambercript_for_lawyers.backend.model.Subscription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByAdminId(Long adminId);

    @Query("SELECT s FROM Subscription s JOIN FETCH s.admin a WHERE a.email = :query OR a.nic = :query")
    List<Subscription> findSubscriptionsByAdminEmailOrNic(@Param("query") String query);

    Page<Subscription> findByIsActiveFalse(Pageable pageable);
}
