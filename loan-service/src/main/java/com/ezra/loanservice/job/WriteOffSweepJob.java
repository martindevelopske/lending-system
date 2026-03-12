package com.ezra.loanservice.job;

import com.ezra.loanservice.LoanRepository;
import com.ezra.loanservice.enums.LoanState;
import com.ezra.loanservice.event.LoanEventPublisher;
import com.ezra.loanservice.event.LoanEventType;
import com.ezra.loanservice.models.Loan;
import com.ezra.loanservice.services.LoanStateMachine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class WriteOffSweepJob {
    private final LoanRepository loanRepository;
    private final LoanStateMachine stateMachine;
    private final LoanEventPublisher eventPublisher;

    @Value("${loan.write-off.days-threshold:90}")
    private int writeOffDaysThreshold;

    @Scheduled(cron = "0 0 3 * * *") // 3 AM daily
    @Transactional
    public void sweepWriteOffLoans() {
        log.info("Starting write-off sweep job (threshold: {} days)...", writeOffDaysThreshold);
        LocalDate threshold = LocalDate.now().minusDays(writeOffDaysThreshold);
        List<Loan> loansToWriteOff = loanRepository.findOverdueLoansForWriteOff(threshold);

        int count = 0;
        for (Loan loan : loansToWriteOff) {
            loan.setState(stateMachine.transition(loan.getState(), LoanState.WRITTEN_OFF));
            loanRepository.save(loan);
            eventPublisher.publishLoanEvent(LoanEventType.LOAN_WRITTEN_OFF, loan);
            count++;
        }

        log.info("Write-off sweep completed. Written off {} loans.", count);
    }
}
