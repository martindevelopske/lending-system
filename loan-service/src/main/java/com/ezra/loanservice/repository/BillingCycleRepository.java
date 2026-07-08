package com.ezra.loanservice.repository;

import com.ezra.loanservice.models.BillingCycle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BillingCycleRepository extends JpaRepository<BillingCycle, UUID> {

    Optional<BillingCycle> findByCustomerId(UUID customerId);
}
