package com.ezra.loanservice.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingCycleResponse {
    private UUID id;
    private UUID customerId;
    private Integer billingDayOfMonth;
    private LocalDate nextBillingDate;
}
