package com.ezra.loanservice.dto;

import com.ezra.loanservice.enums.InstallmentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstallmentResponse {
    private UUID id;
    private Integer installmentNumber;
    private BigDecimal amountDue;
    private BigDecimal amountPaid;
    private BigDecimal outstanding;
    private LocalDate dueDate;
    private InstallmentStatus status;
}
