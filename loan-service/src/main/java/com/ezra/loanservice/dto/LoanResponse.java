package com.ezra.loanservice.dto;


import com.ezra.loanservice.enums.LoanState;
import com.ezra.loanservice.enums.LoanStructure;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanResponse {
    private UUID id;
    private UUID customerId;
    private UUID productId;
    private String productName;
    private BigDecimal principalAmount;
    private BigDecimal interestRate;
    private BigDecimal serviceFee;
    private BigDecimal totalAmount;
    private BigDecimal amountPaid;
    private BigDecimal outstandingBalance;
    private LoanState state;
    private LoanStructure loanStructure;
    private Integer tenureValue;
    private String tenureType;
    private LocalDate disbursementDate;
    private LocalDate dueDate;
//    private List<InstallmentResponse> installments;
//    private List<RepaymentResponse> repayments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
