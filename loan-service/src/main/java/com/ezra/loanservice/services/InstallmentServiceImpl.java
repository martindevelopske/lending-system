package com.ezra.loanservice.services;

import com.ezra.loanservice.enums.InstallmentStatus;
import com.ezra.loanservice.models.Installment;
import com.ezra.loanservice.models.Loan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class InstallmentServiceImpl implements InstallmentService {
    @Override
    public List<Installment> generateInstallments(Loan loan, Integer numberOfInstallments) {
        List<Installment> installments = new ArrayList<>();
        BigDecimal installmentAmount = loan.getTotalAmount().divide(new BigDecimal(numberOfInstallments), 4, RoundingMode.HALF_UP);

        BigDecimal allocated = BigDecimal.ZERO;

        for (int i=1; i <= numberOfInstallments; i++) {
            BigDecimal amount = (i == numberOfInstallments) ? loan.getTotalAmount().subtract(allocated) : installmentAmount;

            LocalDate dueDate = calculateDueDate(loan.getDisbursementDate(), i, loan.getTenureType());

            Installment installment = Installment.builder()
                    .loan(loan)
                    .installmentNumber(i)
                    .amountDue(amount)
                    .dueDate(dueDate)
                    .amountPaid(BigDecimal.ZERO)
                    .status(InstallmentStatus.PENDING)
                    .build();

            installments.add(installment);
            allocated = allocated.add(amount);

        }
        log.info("Generated {} installments for loan: {}", installments.size(), loan.getId());
        return installments;

    }

    private LocalDate calculateDueDate(LocalDate disbursementDate, int installmentNumber, String tenureType) {
        return switch (tenureType) {
            case "DAYS" -> disbursementDate.plusDays(installmentNumber);
            case "WEEKS" -> disbursementDate.plusWeeks(installmentNumber);
            case "MONTHS" -> disbursementDate.plusMonths(installmentNumber);

            default -> disbursementDate.plusMonths(installmentNumber);
        };
    }
}
