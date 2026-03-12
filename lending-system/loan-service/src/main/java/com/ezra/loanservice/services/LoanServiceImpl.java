package com.ezra.loanservice.services;

import com.ezra.loanservice.dto.LoanCreateRequest;
import com.ezra.loanservice.dto.LoanResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanServiceImpl implements LoanService {
    @Override
    public LoanResponse getLoan(UUID id) {
        return null;
    }

    @Override
    public List<LoanResponse> getCustomerLoans(UUID customerId) {
        return List.of();
    }

    @Override
    public LoanResponse disburseLoan(LoanCreateRequest request) {
        return null;
    }
}
