package com.ezra.loanservice.services;

import com.ezra.loanservice.LoanRepository;
import com.ezra.loanservice.dto.RepaymentRequest;
import com.ezra.loanservice.dto.RepaymentResponse;
import com.ezra.loanservice.enums.InstallmentStatus;
import com.ezra.loanservice.enums.LoanState;
import com.ezra.loanservice.event.LoanEventPublisher;
import com.ezra.loanservice.event.LoanEventType;
import com.ezra.loanservice.exceptions.InvalidStateTransitionException;
import com.ezra.loanservice.exceptions.LoanNotFoundException;
import com.ezra.loanservice.models.Installment;
import com.ezra.loanservice.models.Loan;
import com.ezra.loanservice.models.Repayment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RepaymentServiceImpl implements RepaymentService {

    private final LoanRepository loanRepository;
    private final LoanStateMachine loanStateMachine;
    private final LoanEventPublisher loanEventPublisher;

    @Override
    @Transactional
    public RepaymentResponse makeRepayment(UUID loanId, RepaymentRequest request) {
        Loan loan = loanRepository.findByIdWithDetails(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found: " + loanId));

        if (loanStateMachine.isTerminalState(loan.getState())) {
            throw new InvalidStateTransitionException(
                    "Cannot make repayment on loan in " + loan.getState() + " state");
        }

        BigDecimal paymentAmount = request.getAmount();
        BigDecimal outstanding = loan.getOutstandingBalance();

        if (paymentAmount.compareTo(outstanding) > 0) {
            paymentAmount = outstanding;
        }

        Repayment repayment = Repayment.builder()
                .loan(loan)
                .amount(paymentAmount)
                .paymentMethod(request.getPaymentMethod())
                .paymentReference(request.getPaymentReference())
                .build();

        loan.getRepayments().add(repayment);
        loan.setAmountPaid(loan.getAmountPaid().add(paymentAmount));

        // Allocate payment to installments if applicable
        if (!loan.getInstallments().isEmpty()) {
            allocateToInstallments(loan.getInstallments(), paymentAmount);
        }

        // Close loan if fully paid
        if (loan.isFullyPaid()) {
            loanStateMachine.transition(loan.getState(), LoanState.CLOSED);
            loan.setState(LoanState.CLOSED);
            log.info("Loan {} fully paid and closed", loanId);
        }

        loan = loanRepository.save(loan);

        // Publish event
        String eventType = loan.getState() == LoanState.CLOSED
                ? LoanEventType.LOAN_CLOSED
                : LoanEventType.LOAN_REPAYMENT;
        loanEventPublisher.publishLoanEvent(eventType, loan);

        log.info("Repayment of {} recorded for loan {}", paymentAmount, loanId);

        return RepaymentResponse.builder()
                .id(repayment.getId())
                .amount(repayment.getAmount())
                .paymentMethod(repayment.getPaymentMethod())
                .paymentReference(repayment.getPaymentReference())
                .paidAt(repayment.getPaidAt())
                .build();
    }

    private void allocateToInstallments(List<Installment> installments, BigDecimal payment) {
        BigDecimal remaining = payment;

        List<Installment> sorted = installments.stream()
                .filter(i -> i.getStatus() != InstallmentStatus.PAID)
                .sorted(Comparator.comparing(Installment::getInstallmentNumber))
                .toList();

        for (Installment installment : sorted) {
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) break;

            BigDecimal installmentOutstanding = installment.getOutstanding();
            BigDecimal allocation = remaining.min(installmentOutstanding);

            installment.setAmountPaid(installment.getAmountPaid().add(allocation));
            remaining = remaining.subtract(allocation);

            if (installment.getAmountPaid().compareTo(installment.getAmountDue()) >= 0) {
                installment.setStatus(InstallmentStatus.PAID);
            } else {
                installment.setStatus(InstallmentStatus.PARTIALLY_PAID);
            }
        }
    }
}
