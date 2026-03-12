package com.ezra.loanservice.mappers;

import com.ezra.loanservice.dto.LoanResponse;
import com.ezra.loanservice.models.Loan;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LoanMapper {
    private final InstallmentMapper installmentMapper;
    private final RepaymentMapper repaymentMapper;

    public LoanResponse toResponse(Loan loan) {
        return LoanResponse.builder()
                .id(loan.getId())
                .customerId(loan.getCustomerId())
                .productId(loan.getProductId())
                .productName(loan.getProductName())
                .principalAmount(loan.getPrincipalAmount())
                .interestRate(loan.getInterestRate())
                .serviceFee(loan.getServiceFee())
                .totalAmount(loan.getTotalAmount())
                .amountPaid(loan.getAmountPaid())
                .outstandingBalance(loan.getOutstandingBalance())
                .state(loan.getState())
                .loanStructure(loan.getLoanStructure())
                .tenureValue(loan.getTenureValue())
                .tenureType(loan.getTenureType())
                .disbursementDate(loan.getDisbursementDate())
                .dueDate(loan.getDueDate())
                .installments(loan.getInstallments() != null
                        ? loan.getInstallments().stream().map(installmentMapper::toResponse).collect(Collectors.toList())
                        : null)
                .repayments(loan.getRepayments() != null
                        ? loan.getRepayments().stream().map(repaymentMapper::toResponse).collect(Collectors.toList())
                        : null)
                .createdAt(loan.getCreatedAt())
                .updatedAt(loan.getUpdatedAt())
                .build();
    }
}
