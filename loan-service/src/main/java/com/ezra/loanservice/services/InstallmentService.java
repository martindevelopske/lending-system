package com.ezra.loanservice.services;

import com.ezra.loanservice.models.Installment;
import com.ezra.loanservice.models.Loan;

import java.util.List;

public interface InstallmentService {
    List<Installment> generateInstallments(Loan loan, Integer tenureValue);
}
