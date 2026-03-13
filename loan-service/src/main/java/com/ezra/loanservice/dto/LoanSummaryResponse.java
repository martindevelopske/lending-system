package com.ezra.loanservice.dto;

import lombok.*;

import java.math.BigDecimal;

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
}
