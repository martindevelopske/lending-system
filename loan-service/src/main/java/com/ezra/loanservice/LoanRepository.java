package com.ezra.loanservice;

import com.ezra.loanservice.enums.LoanState;
import com.ezra.loanservice.models.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoanRepository extends JpaRepository<Loan, UUID> {

    @Query("SELECT l FROM Loan l LEFT JOIN FETCH l.installments LEFT JOIN FETCH l.repayments WHERE l.id = :id")
    Optional<Loan> findByIdWithDetails(UUID id);

    List<Loan> findByCustomerId(UUID customerId);

    @Query("SELECT l FROM Loan l WHERE l.state = 'OPEN' AND l.dueDate < :date")
    List<Loan> findOpenLoansOverdue(LocalDate date);

    @Query("SELECT l FROM Loan l WHERE l.state = 'OVERDUE' AND l.dueDate < :date")
    List<Loan> findOverdueLoansForWriteOff(LocalDate date);

    List<Loan> findByState(LoanState state);

    @Query("SELECT l FROM Loan l WHERE l.state IN ('OPEN', 'OVERDUE') AND (l.lastFeeAccrualDate IS NULL OR l.lastFeeAccrualDate < :date)")
    List<Loan> findActiveLoansForDailyFeeAccrual(LocalDate date);
}
