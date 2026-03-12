package com.ezra.loanservice.services;

import java.util.UUID;

public interface RepaymentService {
    RepaymentResponse makeRepayment(UUID loanId, RepaymentRequest request);
}
