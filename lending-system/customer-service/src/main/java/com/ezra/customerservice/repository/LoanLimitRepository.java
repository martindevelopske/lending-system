package com.ezra.customerservice.repository;

import com.ezra.customerservice.models.LoanLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LoanLimitRepository extends JpaRepository<LoanLimit, UUID> {
    Optional<LoanLimit> findByCustomerId(UUID customerId);
}
