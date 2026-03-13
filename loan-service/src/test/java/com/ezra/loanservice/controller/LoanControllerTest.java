package com.ezra.loanservice.controller;

import com.ezra.loanservice.dto.*;
import com.ezra.loanservice.enums.LoanState;
import com.ezra.loanservice.enums.LoanStructure;
import com.ezra.loanservice.exceptions.InvalidStateTransitionException;
import com.ezra.loanservice.exceptions.LoanNotFoundException;
import com.ezra.loanservice.services.BillingService;
import com.ezra.loanservice.services.LoanService;
import com.ezra.loanservice.services.RepaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoanController.class)
class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LoanService loanService;

    @MockBean
    private RepaymentService repaymentService;

    @MockBean
    private BillingService billingService;

    private LoanResponse sampleLoan() {
        return LoanResponse.builder()
                .id(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .productId(UUID.randomUUID())
                .productName("Nano Loan")
                .principalAmount(new BigDecimal("2000"))
                .interestRate(new BigDecimal("0.05"))
                .serviceFee(new BigDecimal("50"))
                .totalAmount(new BigDecimal("2050"))
                .amountPaid(BigDecimal.ZERO)
                .outstandingBalance(new BigDecimal("2050"))
                .state(LoanState.OPEN)
                .loanStructure(LoanStructure.LUMP_SUM)
                .tenureValue(30)
                .tenureType("DAYS")
                .disbursementDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(30))
                .installments(List.of())
                .repayments(List.of())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // --- POST /api/v1/loans ---

    @Test
    void disburseLoan_validRequest_returns201() throws Exception {
        LoanCreateRequest request = LoanCreateRequest.builder()
                .customerId(UUID.randomUUID())
                .productId(UUID.randomUUID())
                .amount(new BigDecimal("2000"))
                .build();

        LoanResponse response = sampleLoan();
        when(loanService.disburseLoan(any(LoanCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productName").value("Nano Loan"))
                .andExpect(jsonPath("$.state").value("OPEN"))
                .andExpect(jsonPath("$.principalAmount").value(2000));
    }

    @Test
    void disburseLoan_missingCustomerId_returns400() throws Exception {
        LoanCreateRequest request = LoanCreateRequest.builder()
                .productId(UUID.randomUUID())
                .amount(new BigDecimal("2000"))
                .build();

        mockMvc.perform(post("/api/v1/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.customerId").exists());
    }

    @Test
    void disburseLoan_zeroAmount_returns400() throws Exception {
        LoanCreateRequest request = LoanCreateRequest.builder()
                .customerId(UUID.randomUUID())
                .productId(UUID.randomUUID())
                .amount(new BigDecimal("0"))
                .build();

        mockMvc.perform(post("/api/v1/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // --- GET /api/v1/loans/{id} ---

    @Test
    void getLoan_existingLoan_returnsLoan() throws Exception {
        UUID id = UUID.randomUUID();
        LoanResponse response = sampleLoan();
        response.setId(id);
        when(loanService.getLoan(id)).thenReturn(response);

        mockMvc.perform(get("/api/v1/loans/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.state").value("OPEN"));
    }

    @Test
    void getLoan_notFound_returns404() throws Exception {
        UUID id = UUID.randomUUID();
        when(loanService.getLoan(id)).thenThrow(new LoanNotFoundException("Loan not found: " + id));

        mockMvc.perform(get("/api/v1/loans/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Loan not found: " + id));
    }

    // --- GET /api/v1/loans ---

    @Test
    void getAllLoans_returnsList() throws Exception {
        List<LoanResponse> loans = List.of(sampleLoan(), sampleLoan());
        when(loanService.getAllLoans()).thenReturn(loans);

        mockMvc.perform(get("/api/v1/loans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    // --- GET /api/v1/loans/customer/{customerId} ---

    @Test
    void getCustomerLoans_returnsList() throws Exception {
        UUID customerId = UUID.randomUUID();
        LoanResponse loan = sampleLoan();
        loan.setCustomerId(customerId);
        when(loanService.getCustomerLoans(customerId)).thenReturn(List.of(loan));

        mockMvc.perform(get("/api/v1/loans/customer/{customerId}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getCustomerLoans_noLoans_returnsEmptyList() throws Exception {
        UUID customerId = UUID.randomUUID();
        when(loanService.getCustomerLoans(customerId)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/loans/customer/{customerId}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // --- POST /api/v1/loans/{loanId}/repayments ---

    @Test
    void makeRepayment_validRequest_returns201() throws Exception {
        UUID loanId = UUID.randomUUID();
        RepaymentRequest request = RepaymentRequest.builder()
                .amount(new BigDecimal("500"))
                .paymentMethod("MPESA")
                .paymentReference("PAY-001")
                .build();

        RepaymentResponse response = RepaymentResponse.builder()
                .id(UUID.randomUUID())
                .amount(new BigDecimal("500"))
                .paymentMethod("MPESA")
                .paymentReference("PAY-001")
                .paidAt(LocalDateTime.now())
                .build();
        when(repaymentService.makeRepayment(eq(loanId), any(RepaymentRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/loans/{loanId}/repayments", loanId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(500))
                .andExpect(jsonPath("$.paymentMethod").value("MPESA"))
                .andExpect(jsonPath("$.paymentReference").value("PAY-001"));
    }

    @Test
    void makeRepayment_missingPaymentReference_returns400() throws Exception {
        UUID loanId = UUID.randomUUID();
        RepaymentRequest request = RepaymentRequest.builder()
                .amount(new BigDecimal("500"))
                .paymentMethod("MPESA")
                .build();

        mockMvc.perform(post("/api/v1/loans/{loanId}/repayments", loanId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void makeRepayment_loanNotFound_returns404() throws Exception {
        UUID loanId = UUID.randomUUID();
        RepaymentRequest request = RepaymentRequest.builder()
                .amount(new BigDecimal("500"))
                .paymentMethod("MPESA")
                .paymentReference("PAY-002")
                .build();

        when(repaymentService.makeRepayment(eq(loanId), any(RepaymentRequest.class)))
                .thenThrow(new LoanNotFoundException("Loan not found"));

        mockMvc.perform(post("/api/v1/loans/{loanId}/repayments", loanId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void makeRepayment_closedLoan_returns400() throws Exception {
        UUID loanId = UUID.randomUUID();
        RepaymentRequest request = RepaymentRequest.builder()
                .amount(new BigDecimal("500"))
                .paymentMethod("MPESA")
                .paymentReference("PAY-003")
                .build();

        when(repaymentService.makeRepayment(eq(loanId), any(RepaymentRequest.class)))
                .thenThrow(new InvalidStateTransitionException("Cannot make repayment on loan in CLOSED state"));

        mockMvc.perform(post("/api/v1/loans/{loanId}/repayments", loanId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // --- GET /api/v1/loans/customer/{customerId}/summary ---

    @Test
    void getCustomerSummary_returnsConsolidatedBilling() throws Exception {
        UUID customerId = UUID.randomUUID();
        LoanSummaryResponse summary = LoanSummaryResponse.builder()
                .totalLoans(3)
                .activeLoans(1)
                .overdueLoans(1)
                .totalDisbursed(new BigDecimal("52500"))
                .totalOutstanding(new BigDecimal("43416.66"))
                .build();
        when(billingService.getCustomerSummary(customerId)).thenReturn(summary);

        mockMvc.perform(get("/api/v1/loans/customer/{customerId}/summary", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalLoans").value(3))
                .andExpect(jsonPath("$.activeLoans").value(1))
                .andExpect(jsonPath("$.overdueLoans").value(1))
                .andExpect(jsonPath("$.totalDisbursed").value(52500))
                .andExpect(jsonPath("$.totalOutstanding").value(43416.66));
    }
}
