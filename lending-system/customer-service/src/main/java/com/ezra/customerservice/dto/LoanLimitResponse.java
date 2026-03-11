package com.ezra.customerservice.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanLimitResponse {
    private UUID id;
    private BigDecimal maxLoanAmount;
    private BigDecimal availableAmount;
}