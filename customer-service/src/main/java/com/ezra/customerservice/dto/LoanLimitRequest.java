package com.ezra.customerservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanLimitRequest {
    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal maxLoanAmount;
}

