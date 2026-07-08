package com.ezra.loanservice.services;

import com.ezra.loanservice.LoanRepository;
import com.ezra.loanservice.dto.BillingCycleRequest;
import com.ezra.loanservice.dto.BillingCycleResponse;
import com.ezra.loanservice.dto.LoanSummaryResponse;
import com.ezra.loanservice.enums.InstallmentStatus;
import com.ezra.loanservice.enums.LoanState;
import com.ezra.loanservice.models.BillingCycle;
import com.ezra.loanservice.models.Installment;
import com.ezra.loanservice.models.Loan;
import com.ezra.loanservice.repository.BillingCycleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillingServiceImpl implements BillingService {
    private final LoanRepository loanRepository;
    private final BillingCycleRepository billingCycleRepository;

    /**
     * Returns a consolidated billing summary for a customer including:
     * - Loan counts and totals (existing)
     * - Accrued fees total
     * - Billing cycle day (if set)
     * - Next due date (earliest upcoming due date across all active loans)
     * - Upcoming unpaid installments sorted by due date
     */
    @Override
    @Transactional(readOnly = true)
    public LoanSummaryResponse getCustomerSummary(UUID customerId) {
        List<Loan> loans = loanRepository.findByCustomerId(customerId);

        List<Loan> activeLoans = loans.stream()
                .filter(l -> l.getState() == LoanState.OPEN || l.getState() == LoanState.OVERDUE)
                .toList();

        long openCount = loans.stream().filter(l -> l.getState() == LoanState.OPEN).count();
        long overdueCount = loans.stream().filter(l -> l.getState() == LoanState.OVERDUE).count();

        BigDecimal totalDisbursed = loans.stream()
                .map(Loan::getPrincipalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalOutstanding = activeLoans.stream()
                .map(Loan::getOutstandingBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalAccruedFees = activeLoans.stream()
                .map(l -> l.getAccruedDailyFees().add(l.getAccruedLateFees()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Find the next due date across all active loans
        LocalDate nextDueDate = findNextDueDate(activeLoans);

        // Get billing cycle if set
        Integer billingDay = billingCycleRepository.findByCustomerId(customerId)
                .map(BillingCycle::getBillingDayOfMonth)
                .orElse(null);

        // Collect upcoming unpaid installments across all active loans
        List<LoanSummaryResponse.UpcomingInstallment> upcomingInstallments = buildUpcomingInstallments(activeLoans);

        return LoanSummaryResponse.builder()
                .totalLoans(loans.size())
                .activeLoans(openCount)
                .overdueLoans(overdueCount)
                .totalDisbursed(totalDisbursed)
                .totalOutstanding(totalOutstanding)
                .totalAccruedFees(totalAccruedFees)
                .nextDueDate(nextDueDate)
                .billingDayOfMonth(billingDay)
                .upcomingInstallments(upcomingInstallments)
                .build();
    }

    /**
     * Sets or updates the customer's preferred billing day. All future INSTALLMENT
     * loans for this customer will have their installment due dates aligned to this day.
     * Day is restricted to 1-28 to avoid month-end edge cases.
     */
    @Override
    @Transactional
    public BillingCycleResponse setBillingCycle(UUID customerId, BillingCycleRequest request) {
        BillingCycle cycle = billingCycleRepository.findByCustomerId(customerId)
                .orElse(BillingCycle.builder().customerId(customerId).build());

        cycle.setBillingDayOfMonth(request.getBillingDayOfMonth());
        cycle = billingCycleRepository.save(cycle);

        log.info("Set billing cycle for customer {}: day {} of each month", customerId, request.getBillingDayOfMonth());

        return toBillingCycleResponse(cycle);
    }

    @Override
    @Transactional(readOnly = true)
    public BillingCycleResponse getBillingCycle(UUID customerId) {
        BillingCycle cycle = billingCycleRepository.findByCustomerId(customerId)
                .orElse(null);

        if (cycle == null) {
            return BillingCycleResponse.builder()
                    .customerId(customerId)
                    .billingDayOfMonth(null)
                    .nextBillingDate(null)
                    .build();
        }

        return toBillingCycleResponse(cycle);
    }

    /**
     * Finds the nearest upcoming due date across all active loans.
     * For LUMP_SUM loans, uses the loan's dueDate.
     * For INSTALLMENT loans, uses the earliest unpaid installment's dueDate.
     */
    private LocalDate findNextDueDate(List<Loan> activeLoans) {
        LocalDate today = LocalDate.now();
        LocalDate earliest = null;

        for (Loan loan : activeLoans) {
            if (loan.getInstallments() != null && !loan.getInstallments().isEmpty()) {
                // INSTALLMENT loan: find the earliest unpaid installment due date
                Optional<LocalDate> nextInstallment = loan.getInstallments().stream()
                        .filter(i -> i.getStatus() != InstallmentStatus.PAID)
                        .map(Installment::getDueDate)
                        .min(Comparator.naturalOrder());
                if (nextInstallment.isPresent()) {
                    LocalDate date = nextInstallment.get();
                    if (earliest == null || date.isBefore(earliest)) {
                        earliest = date;
                    }
                }
            } else {
                // LUMP_SUM loan: use the loan's due date
                if (earliest == null || loan.getDueDate().isBefore(earliest)) {
                    earliest = loan.getDueDate();
                }
            }
        }

        return earliest;
    }

    /**
     * Collects all unpaid installments across all active loans, sorted by due date.
     * This gives the customer a consolidated view of their upcoming payments.
     */
    private List<LoanSummaryResponse.UpcomingInstallment> buildUpcomingInstallments(List<Loan> activeLoans) {
        return activeLoans.stream()
                .filter(loan -> loan.getInstallments() != null && !loan.getInstallments().isEmpty())
                .flatMap(loan -> loan.getInstallments().stream()
                        .filter(i -> i.getStatus() != InstallmentStatus.PAID)
                        .map(i -> LoanSummaryResponse.UpcomingInstallment.builder()
                                .loanProductName(loan.getProductName())
                                .installmentNumber(i.getInstallmentNumber())
                                .amountDue(i.getAmountDue())
                                .amountPaid(i.getAmountPaid())
                                .outstanding(i.getOutstanding())
                                .dueDate(i.getDueDate())
                                .build()))
                .sorted(Comparator.comparing(LoanSummaryResponse.UpcomingInstallment::getDueDate))
                .collect(Collectors.toList());
    }

    private BillingCycleResponse toBillingCycleResponse(BillingCycle cycle) {
        return BillingCycleResponse.builder()
                .id(cycle.getId())
                .customerId(cycle.getCustomerId())
                .billingDayOfMonth(cycle.getBillingDayOfMonth())
                .nextBillingDate(calculateNextBillingDate(cycle.getBillingDayOfMonth()))
                .build();
    }

    /**
     * Calculates the next billing date from today based on the billing day.
     * If today is before the billing day this month, returns this month's billing date.
     * Otherwise, returns next month's billing date.
     */
    static LocalDate calculateNextBillingDate(int billingDay) {
        LocalDate today = LocalDate.now();
        LocalDate thisMonth = today.withDayOfMonth(billingDay);
        return today.isBefore(thisMonth) || today.isEqual(thisMonth) ? thisMonth : thisMonth.plusMonths(1);
    }
}
