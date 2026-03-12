package com.ezra.loanservice.mappers;

import com.ezra.loanservice.dto.InstallmentResponse;
import com.ezra.loanservice.models.Installment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InstallmentMapper {
    public InstallmentResponse toResponse(Installment installment) {
        return InstallmentResponse.builder()
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
