package com.ezra.customerservice.service;

import com.ezra.customerservice.dto.LoanLimitCheckRequest;
import com.ezra.customerservice.dto.LoanLimitCheckResponse;
import com.ezra.customerservice.dto.LoanLimitRequest;
import com.ezra.customerservice.dto.LoanLimitResponse;
import jakarta.validation.Valid;

import java.util.UUID;

public interface LoanLimitService {
    LoanLimitResponse setLoanLimit(UUID customerId, @Valid LoanLimitRequest request);

    LoanLimitResponse getLoanLimit(UUID customerId);

    LoanLimitCheckResponse checkLoanLimit(UUID customerId, LoanLimitCheckRequest request);
}
