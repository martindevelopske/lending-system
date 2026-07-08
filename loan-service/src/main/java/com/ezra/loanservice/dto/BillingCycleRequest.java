package com.ezra.loanservice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingCycleRequest {

    @NotNull(message = "Billing day of month is required")
    @Min(value = 1, message = "Billing day must be between 1 and 28")
    @Max(value = 28, message = "Billing day must be between 1 and 28")
    private Integer billingDayOfMonth;
}
