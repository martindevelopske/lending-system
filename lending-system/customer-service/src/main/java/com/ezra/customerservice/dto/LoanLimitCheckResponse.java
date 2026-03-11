package com.ezra.customerservice.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanLimitCheckResponse {
    private boolean eligible;
    private BigDecimal availableAmount;
    private BigDecimal requestedAmount;
    private String message;

}
