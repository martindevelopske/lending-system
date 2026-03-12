package com.ezra.loanservice.services;

import com.ezra.loanservice.dto.LoanCreateRequest;
import com.ezra.loanservice.dto.LoanResponse;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface LoanService {
    LoanResponse getLoan(UUID id);

    List<LoanResponse> getCustomerLoans(UUID customerId);

    LoanResponse disburseLoan(@Valid LoanCreateRequest request);

    List<LoanResponse> getAllLoans();
}
