package com.ezra.loanservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepaymentRequest {

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal amount;


    private String paymentMethod;

    @NotBlank(message = "Payment ref is required")
    private String paymentReference;
}
