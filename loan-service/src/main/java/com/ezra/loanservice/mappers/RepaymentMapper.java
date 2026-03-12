package com.ezra.loanservice.mappers;

import com.ezra.loanservice.dto.RepaymentResponse;
import com.ezra.loanservice.models.Repayment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RepaymentMapper {
    public RepaymentResponse toResponse(Repayment repayment){
        return RepaymentResponse.builder()
                .id(repayment.getId())
                .amount(repayment.getAmount())
                .paymentReference(repayment.getPaymentReference())
                .paymentMethod(repayment.getPaymentMethod())
                .paidAt(repayment.getPaidAt())
                .build();
    }
}
