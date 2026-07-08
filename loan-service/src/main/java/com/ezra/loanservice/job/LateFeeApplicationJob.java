package com.ezra.loanservice.job;

import com.ezra.loanservice.LoanRepository;
import com.ezra.loanservice.client.ProductClient;
import com.ezra.loanservice.event.LoanEventPublisher;
import com.ezra.loanservice.event.LoanEventType;
import com.ezra.loanservice.models.Loan;
import com.ezra.loanservice.services.FeeCalculationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

/**
 * Scheduled job that applies late fees on overdue loans.
 * Runs daily at 2:00 AM. For each overdue loan, fetches the product's fee
 * configuration, checks if any LATE fees have met their daysAfterDue threshold,
 * and applies the fee to the loan's accrued late fees.
 * Tracks last late fee date per loan to apply fees only once per day.
 * Errors on individual loans are caught and logged without stopping the batch.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LateFeeApplicationJob {

    private final LoanRepository loanRepository;
    private final ProductClient productClient;
    private final FeeCalculationService feeCalculationService;
    private final LoanEventPublisher eventPublisher;

    @Scheduled(cron = "0 0 2 * * *") // 2 AM daily
    @Transactional
    public void applyLateFees() {
        log.info("Starting late fee application job...");
        LocalDate today = LocalDate.now();
        List<Loan> overdueLoans = loanRepository.findOverdueLoansForLateFee(today);

        int count = 0;
        for (Loan loan : overdueLoans) {
            try {
                Map<String, Object> product = productClient.getProduct(loan.getProductId());
                List<Map<String, Object>> fees = (List<Map<String, Object>>) product.get("fees");

                if (fees == null) continue;

                long daysOverdue = ChronoUnit.DAYS.between(loan.getDueDate(), today);
                BigDecimal lateFee = feeCalculationService.calculateLateFee(
                        loan.getOutstandingBalance(), fees, daysOverdue);

                if (lateFee.compareTo(BigDecimal.ZERO) > 0) {
                    loan.setAccruedLateFees(loan.getAccruedLateFees().add(lateFee));
                    loan.setLastLateFeeDate(today);
                    loanRepository.save(loan);
                    eventPublisher.publishLoanEvent(LoanEventType.LATE_FEE_APPLIED, loan);
                    count++;
                    log.debug("Applied late fee {} for loan {} (days overdue: {})",
                            lateFee, loan.getId(), daysOverdue);
                } else {
                    loan.setLastLateFeeDate(today);
                    loanRepository.save(loan);
                }
            } catch (Exception e) {
                log.error("Error applying late fee for loan {}", loan.getId(), e);
            }
        }
        log.info("Late fee application completed. Applied late fees to {} loans.", count);
    }
}
