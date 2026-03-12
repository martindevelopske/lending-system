package com.ezra.loanservice.services;

import com.ezra.loanservice.dto.RepaymentRequest;
import com.ezra.loanservice.dto.RepaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RepaymentServiceImpl implements RepaymentService{
    @Override
    public RepaymentResponse makeRepayment(UUID loanId, RepaymentRequest request) {
        return null;
    }
}
