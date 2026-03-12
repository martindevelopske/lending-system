package com.ezra.loanservice.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepaymentResponse {
    private UUID id;
    private BigDecimal amount;
    private String paymentMethod;
    private String paymentReference;
    private LocalDateTime paidAt;

}
