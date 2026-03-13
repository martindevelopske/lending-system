package com.ezra.customerservice.controller;

import com.ezra.customerservice.dto.*;
import com.ezra.customerservice.service.CustomerService;
import com.ezra.customerservice.service.LoanLimitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for customer management and loan limit operations.
 * Provides endpoints for customer CRUD, loan limit configuration,
 * and eligibility checks used by the loan-service during disbursement.
 */
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;
    private final LoanLimitService loanLimitService;

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CustomerCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.createCustomer(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomer(@PathVariable UUID id) {
        return ResponseEntity.ok(customerService.getCustomer(id));
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(@PathVariable UUID id,
                                                           @Valid @RequestBody CustomerUpdateRequest request) {
        return ResponseEntity.ok(customerService.updateCustomer(id, request));
    }

    @PutMapping("/{customerId}/loan-limit")
    public ResponseEntity<LoanLimitResponse> setLoanLimit(@PathVariable UUID customerId,
                                                          @Valid @RequestBody LoanLimitRequest request) {
        return ResponseEntity.ok(loanLimitService.setLoanLimit(customerId, request));
    }

    @GetMapping("/{customerId}/loan-limit")
    public ResponseEntity<LoanLimitResponse> getLoanLimit(@PathVariable UUID customerId) {
        return ResponseEntity.ok(loanLimitService.getLoanLimit(customerId));
    }

    @PostMapping("/{customerId}/loan-limit/check")
    public ResponseEntity<LoanLimitCheckResponse> checkLoanLimit(@PathVariable UUID customerId,
                                                                 @Valid @RequestBody LoanLimitCheckRequest request) {
        return ResponseEntity.ok(loanLimitService.checkLoanLimit(customerId, request));
    }
}
