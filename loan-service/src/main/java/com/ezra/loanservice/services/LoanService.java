package com.ezra.loanservice.services;

import com.ezra.loanservice.dto.LoanCreateRequest;
import com.ezra.loanservice.dto.LoanResponse;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

@Validated
public interface LoanService {
    LoanResponse getLoan(UUID id);

    List<LoanResponse> getCustomerLoans(UUID customerId);

    LoanResponse disburseLoan(@Valid LoanCreateRequest request);

    List<LoanResponse> getAllLoans();
}
