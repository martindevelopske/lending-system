package com.ezra.loanservice.services;

import com.ezra.loanservice.dto.BillingCycleRequest;
import com.ezra.loanservice.dto.BillingCycleResponse;
import com.ezra.loanservice.dto.LoanSummaryResponse;

import java.util.UUID;

public interface BillingService {
    LoanSummaryResponse getCustomerSummary(UUID customerId);

    BillingCycleResponse setBillingCycle(UUID customerId, BillingCycleRequest request);

    BillingCycleResponse getBillingCycle(UUID customerId);
}
