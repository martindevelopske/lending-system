package com.ezra.loanservice.services;

import com.ezra.loanservice.dto.RepaymentRequest;
import com.ezra.loanservice.dto.RepaymentResponse;

import java.util.UUID;

public interface RepaymentService {
    RepaymentResponse makeRepayment(UUID loanId, RepaymentRequest request);
}
