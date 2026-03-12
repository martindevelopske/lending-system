package com.ezra.loanservice.services;

import java.util.UUID;

public interface BillingService {
    LoanSummaryResponse getCustomerSummary(UUID customerId);
}
