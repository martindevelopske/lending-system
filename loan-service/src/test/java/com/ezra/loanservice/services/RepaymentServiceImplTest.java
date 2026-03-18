package com.ezra.loanservice.services;

import com.ezra.loanservice.LoanRepository;
import com.ezra.loanservice.dto.RepaymentRequest;
import com.ezra.loanservice.dto.RepaymentResponse;
import com.ezra.loanservice.enums.InstallmentStatus;
import com.ezra.loanservice.enums.LoanState;
import com.ezra.loanservice.enums.LoanStructure;
import com.ezra.loanservice.event.LoanEventPublisher;
import com.ezra.loanservice.exceptions.InvalidStateTransitionException;
import com.ezra.loanservice.exceptions.LoanNotFoundException;
import com.ezra.loanservice.models.Installment;
import com.ezra.loanservice.models.Loan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepaymentServiceImplTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private LoanStateMachine loanStateMachine;

    @Mock
    private LoanEventPublisher loanEventPublisher;

    @InjectMocks
    private RepaymentServiceImpl repaymentService;

    private UUID loanId;
    private Loan loan;

    @BeforeEach
    void setUp() {
        loanId = UUID.randomUUID();
        loan = Loan.builder()
                .id(loanId)
                .customerId(UUID.randomUUID())
                .productId(UUID.randomUUID())
                .productName("Test Loan")
                .principalAmount(new BigDecimal("10000"))
                .totalAmount(new BigDecimal("10500"))
                .amountPaid(BigDecimal.ZERO)
                .state(LoanState.OPEN)
                .loanStructure(LoanStructure.LUMP_SUM)
                .tenureValue(30)
                .tenureType("DAYS")
                .interestRate(new BigDecimal("0.05"))
                .disbursementDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(30))
                .installments(new java.util.HashSet<>())
                .repayments(new java.util.HashSet<>())
                .build();
    }

    @Test
    void makeRepayment_partialPayment_updatesAmountPaid() {
        when(loanRepository.findByIdWithDetails(loanId)).thenReturn(Optional.of(loan));
        when(loanStateMachine.isTerminalState(LoanState.OPEN)).thenReturn(false);
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        RepaymentRequest request = RepaymentRequest.builder()
                .amount(new BigDecimal("5000"))
                .paymentReference("PAY-001")
                .paymentMethod("MPESA")
                .build();

        RepaymentResponse response = repaymentService.makeRepayment(loanId, request);

        assertThat(response.getAmount()).isEqualByComparingTo(new BigDecimal("5000"));
        assertThat(loan.getAmountPaid()).isEqualByComparingTo(new BigDecimal("5000"));
        assertThat(loan.getState()).isEqualTo(LoanState.OPEN);
        verify(loanEventPublisher).publishLoanEvent(eq("LOAN_REPAYMENT"), any(Loan.class));
    }

    @Test
    void makeRepayment_fullPayment_closesLoan() {
        when(loanRepository.findByIdWithDetails(loanId)).thenReturn(Optional.of(loan));
        when(loanStateMachine.isTerminalState(LoanState.OPEN)).thenReturn(false);
        when(loanStateMachine.transition(LoanState.OPEN, LoanState.CLOSED)).thenReturn(LoanState.CLOSED);
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        RepaymentRequest request = RepaymentRequest.builder()
                .amount(new BigDecimal("10500"))
                .paymentReference("PAY-002")
                .paymentMethod("MPESA")
                .build();

        repaymentService.makeRepayment(loanId, request);

        assertThat(loan.getState()).isEqualTo(LoanState.CLOSED);
        verify(loanEventPublisher).publishLoanEvent(eq("LOAN_CLOSED"), any(Loan.class));
    }

    @Test
    void makeRepayment_overpayment_cappedAtOutstanding() {
        when(loanRepository.findByIdWithDetails(loanId)).thenReturn(Optional.of(loan));
        when(loanStateMachine.isTerminalState(LoanState.OPEN)).thenReturn(false);
        when(loanStateMachine.transition(LoanState.OPEN, LoanState.CLOSED)).thenReturn(LoanState.CLOSED);
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        RepaymentRequest request = RepaymentRequest.builder()
                .amount(new BigDecimal("99999"))
                .paymentReference("PAY-003")
                .paymentMethod("MPESA")
                .build();

        repaymentService.makeRepayment(loanId, request);

        assertThat(loan.getAmountPaid()).isEqualByComparingTo(new BigDecimal("10500"));
    }

    @Test
    void makeRepayment_onClosedLoan_throwsException() {
        loan.setState(LoanState.CLOSED);
        when(loanRepository.findByIdWithDetails(loanId)).thenReturn(Optional.of(loan));
        when(loanStateMachine.isTerminalState(LoanState.CLOSED)).thenReturn(true);

        RepaymentRequest request = RepaymentRequest.builder()
                .amount(new BigDecimal("1000"))
                .paymentReference("PAY-004")
                .build();

        assertThatThrownBy(() -> repaymentService.makeRepayment(loanId, request))
                .isInstanceOf(InvalidStateTransitionException.class);
    }

    @Test
    void makeRepayment_loanNotFound_throwsException() {
        when(loanRepository.findByIdWithDetails(loanId)).thenReturn(Optional.empty());

        RepaymentRequest request = RepaymentRequest.builder()
                .amount(new BigDecimal("1000"))
                .paymentReference("PAY-005")
                .build();

        assertThatThrownBy(() -> repaymentService.makeRepayment(loanId, request))
                .isInstanceOf(LoanNotFoundException.class);
    }

    @Test
    void makeRepayment_withInstallments_allocatesPayment() {
        Installment inst1 = Installment.builder()
                .installmentNumber(1)
                .amountDue(new BigDecimal("3500"))
                .amountPaid(BigDecimal.ZERO)
                .dueDate(LocalDate.now().plusDays(10))
                .status(InstallmentStatus.PENDING)
                .loan(loan)
                .build();
        Installment inst2 = Installment.builder()
                .installmentNumber(2)
                .amountDue(new BigDecimal("3500"))
                .amountPaid(BigDecimal.ZERO)
                .dueDate(LocalDate.now().plusDays(20))
                .status(InstallmentStatus.PENDING)
                .loan(loan)
                .build();
        Installment inst3 = Installment.builder()
                .installmentNumber(3)
                .amountDue(new BigDecimal("3500"))
                .amountPaid(BigDecimal.ZERO)
                .dueDate(LocalDate.now().plusDays(30))
                .status(InstallmentStatus.PENDING)
                .loan(loan)
                .build();

        loan.setInstallments(new java.util.HashSet<>(java.util.List.of(inst1, inst2, inst3)));

        when(loanRepository.findByIdWithDetails(loanId)).thenReturn(Optional.of(loan));
        when(loanStateMachine.isTerminalState(LoanState.OPEN)).thenReturn(false);
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        RepaymentRequest request = RepaymentRequest.builder()
                .amount(new BigDecimal("5000"))
                .paymentReference("PAY-006")
                .paymentMethod("MPESA")
                .build();

        repaymentService.makeRepayment(loanId, request);

        assertThat(inst1.getStatus()).isEqualTo(InstallmentStatus.PAID);
        assertThat(inst1.getAmountPaid()).isEqualByComparingTo(new BigDecimal("3500"));
        assertThat(inst2.getStatus()).isEqualTo(InstallmentStatus.PARTIALLY_PAID);
        assertThat(inst2.getAmountPaid()).isEqualByComparingTo(new BigDecimal("1500"));
        assertThat(inst3.getStatus()).isEqualTo(InstallmentStatus.PENDING);
    }
}
