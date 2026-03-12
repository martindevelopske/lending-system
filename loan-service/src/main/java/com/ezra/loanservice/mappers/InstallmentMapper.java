package com.ezra.loanservice.mappers;

import com.ezra.loanservice.dto.IntallmentResponse;
import com.ezra.loanservice.models.Installment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InstallmentMapper {
    public IntallmentResponse toResponse(Installment installment) {
        return IntallmentResponse.builder()
                .id(installment.getId())
                .installmentNumber(installment.getInstallmentNumber())
                .amountDue(installment.getAmountDue())
                .amountPaid(installment.getAmountPaid())
                .outstanding(installment.getOutstanding())
                .dueDate(installment.getDueDate())
                .status(installment.getStatus())
                .build();
    }
}
