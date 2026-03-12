package com.ezra.loanservice.controller;

import com.ezra.loanservice.dto.LoanCreateRequest;
import com.ezra.loanservice.dto.LoanResponse;
import com.ezra.loanservice.services.BillingService;
import com.ezra.loanservice.services.LoanService;
import com.ezra.loanservice.services.RepaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
public class LoanController {
    private final LoanService loanService;
    private final RepaymentService repaymentService;
    private final BillingService billingService;

    @PostMapping
    public ResponseEntity<LoanResponse> disburseLoan(@Valid @RequestBody LoanCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(loanService.disburseLoan(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanResponse> getLoan(@PathVariable UUID id) {
        return ResponseEntity.ok(loanService.getLoan(id));
    }

    @GetMapping
    public ResponseEntity<List<LoanResponse>> getAllLoans() {
        return ResponseEntity.ok(loanService.getAllLoans());
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<LoanResponse>> getCustomerLoans(@PathVariable UUID customerId) {
        return ResponseEntity.ok(loanService.getCustomerLoans(customerId));
    }

//    @PostMapping("/{loanId}/repayments")
//    public ResponseEntity<RepaymentResponse> makeRepayment(@PathVariable UUID loanId,
//                                                           @Valid @RequestBody RepaymentRequest request) {
//        return ResponseEntity.status(HttpStatus.CREATED).body(repaymentService.makeRepayment(loanId, request));
//    }
//
//    @GetMapping("/customer/{customerId}/summary")
//    public ResponseEntity<LoanSummaryResponse> getCustomerSummary(@PathVariable UUID customerId) {
//        return ResponseEntity.ok(billingService.getCustomerSummary(customerId));
//    }
}
