package com.ezra.loanservice.services;

import com.ezra.loanservice.dto.LoanSummaryResponse;

import java.util.UUID;

public interface BillingService {
    LoanSummaryResponse getCustomerSummary(UUID customerId);
}
