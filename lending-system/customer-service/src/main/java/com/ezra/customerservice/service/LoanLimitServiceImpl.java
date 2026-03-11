package com.ezra.customerservice.service;

import com.ezra.customerservice.dto.LoanLimitCheckRequest;
import com.ezra.customerservice.dto.LoanLimitCheckResponse;
import com.ezra.customerservice.dto.LoanLimitRequest;
import com.ezra.customerservice.dto.LoanLimitResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanLimitServiceImpl implements LoanLimitService {
    @Override
    public LoanLimitResponse setLoanLimit(UUID customerId, LoanLimitRequest request) {
        return null;
    }

    @Override
    public LoanLimitResponse getLoanLimit(UUID customerId) {
        return null;
    }

    @Override
    public LoanLimitCheckResponse checkLoanLimit(UUID customerId, LoanLimitCheckRequest request) {
        return null;
    }
}
