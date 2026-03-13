package com.ezra.loanservice.job;

import com.ezra.loanservice.LoanRepository;
import com.ezra.loanservice.client.ProductClient;
import com.ezra.loanservice.event.LoanEventPublisher;
import com.ezra.loanservice.event.LoanEventType;
import com.ezra.loanservice.models.Loan;
import com.ezra.loanservice.services.FeeCalculationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Scheduled job that accrues daily fees on all active (OPEN/OVERDUE) loans.
 * Runs daily at 12:30 AM. For each loan, fetches the product's fee configuration
 * and calculates the daily fee based on the current outstanding balance.
 * Tracks last accrual date per loan to prevent duplicate accruals.
 * Errors on individual loans are caught and logged without stopping the batch.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DailyFeeAccrualJob {

    private final LoanRepository loanRepository;
    private final ProductClient productServiceClient;
    private final FeeCalculationService feeCalculationService;
    private final LoanEventPublisher eventPublisher;

    @Scheduled(cron = "0 30 0 * * *")// 12:30 daily
    @Transactional
    public void accrueDailyFees() {
        log.info("Accruing daily fees...");
        LocalDate today = LocalDate.now();
        List<Loan> activeLoans = loanRepository.findActiveLoansForDailyFeeAccrual(today);

        int count = 0;
        for (Loan loan : activeLoans) {
            try {
                Map<String, Object> product = productServiceClient.getProduct(loan.getProductId());

                List<Map<String, Object>> fees = (List<Map<String, Object>>) product.get("fees");

                if (fees == null) continue;

                BigDecimal dailyFee = feeCalculationService.calculateDailyFee(loan.getOutstandingBalance(), fees);
                if (dailyFee.compareTo(BigDecimal.ZERO) > 0) {
                    loan.setAccruedDailyFees(loan.getAccruedDailyFees().add(dailyFee));
                    loan.setLastFeeAccrualDate(today);
                    loanRepository.save(loan);
                    eventPublisher.publishLoanEvent(LoanEventType.DAILY_FEE_ACCRUED, loan);
                    count++;
                    log.debug("Accrued daily fee {} for loan {}", dailyFee, loan.getId());
                } else {
                    loan.setLastFeeAccrualDate(today);
                    loanRepository.save(loan);
                }

            } catch (Exception e) {
                log.error("Error accruing daily fee for loan {}", loan.getId(), e);
            }

        }
        log.info("Daily fee accrual completed. Accrued {} daily fees.", count);
    }

}
