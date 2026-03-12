package com.ezra.loanservice.event;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanEvent {
    private String eventType;
    private UUID loanId;
    private UUID customerId;
    private UUID productId;
    private String productName;
    private BigDecimal amount;
    private BigDecimal outstandingBalance;
    private String loanState;
    private String customerFirstName;
    private String customerLastName;
    private String customerEmail;
    private String customerPhone;
}
