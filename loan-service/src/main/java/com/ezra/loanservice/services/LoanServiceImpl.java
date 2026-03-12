package com.ezra.loanservice.services;

import com.ezra.loanservice.LoanRepository;
import com.ezra.loanservice.dto.LoanCreateRequest;
import com.ezra.loanservice.dto.LoanResponse;
import com.ezra.loanservice.exceptions.LoanNotFoundException;
import com.ezra.loanservice.mappers.LoanMapper;
import com.ezra.loanservice.models.Loan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanServiceImpl implements LoanService {
    private final LoanRepository loanRepository;
    private final LoanMapper loanMapper;

    @Override
    public LoanResponse disburseLoan(LoanCreateRequest request) {
        return null;
    }


    @Transactional(readOnly = true)
    @Override
    public LoanResponse getLoan(UUID id) {
        Loan loan = loanRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found: " + id));
        return loanMapper.toResponse(loan);
    }

    @Transactional(readOnly = true)
    @Override
    public List<LoanResponse> getCustomerLoans(UUID customerId) {
        return loanRepository.findByCustomerId(customerId).stream()
                .map(loanMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<LoanResponse> getAllLoans() {
        return loanRepository.findAll().stream()
                .map(loanMapper::toResponse)
                .collect(Collectors.toList());
    }
}
