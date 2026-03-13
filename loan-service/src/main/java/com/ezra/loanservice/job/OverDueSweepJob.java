package com.ezra.loanservice.job;

import com.ezra.loanservice.LoanRepository;
import com.ezra.loanservice.enums.LoanState;
import com.ezra.loanservice.event.LoanEventPublisher;
import com.ezra.loanservice.event.LoanEventType;
import com.ezra.loanservice.models.Loan;
import com.ezra.loanservice.services.LoanStateMachine;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Scheduled job that marks OPEN loans as OVERDUE when their due date has passed.
 * Runs daily at 1 AM. Transitions each overdue loan via the state machine
 * and publishes LOAN_OVERDUE events to Kafka for notification processing.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OverDueSweepJob {

    private final LoanRepository loanRepository;
    private final LoanStateMachine loanStateMachine;
    private LoanEventPublisher loanEventPublisher;

    @Scheduled(cron = "0 0 1 * * *") //1 am daily
    @Transactional
    public void sweepOverDueLoans() {
        log.info("Sweeping overdue loans...");
        List<Loan> overdueLoans = loanRepository.findOpenLoansOverdue(LocalDate.now());

        int count=0;
        for (Loan loan : overdueLoans){
            loan.setState(loanStateMachine.transition(loan.getState(), LoanState.OVERDUE));
            loanRepository.save(loan);
            loanEventPublisher.publishLoanEvent(LoanEventType.LOAN_OVERDUE, loan);
            count++;
        }

        log.info("Swept {} overdue loans", count);
        log.info("Overdue sweep job completed");
    }
}
