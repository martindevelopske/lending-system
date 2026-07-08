package com.ezra.loanservice.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanSummaryResponse {
    private long totalLoans;
    private long activeLoans;
    private long overdueLoans;
    private BigDecimal totalDisbursed;
    private BigDecimal totalOutstanding;
    private BigDecimal totalAccruedFees;
    private LocalDate nextDueDate;
    private Integer billingDayOfMonth;
    private List<UpcomingInstallment> upcomingInstallments;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpcomingInstallment {
        private String loanProductName;
        private int installmentNumber;
        private BigDecimal amountDue;
        private BigDecimal amountPaid;
        private BigDecimal outstanding;
        private LocalDate dueDate;
    }
}
