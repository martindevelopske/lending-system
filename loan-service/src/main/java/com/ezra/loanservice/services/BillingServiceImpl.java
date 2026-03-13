package com.ezra.loanservice.services;

import com.ezra.loanservice.LoanRepository;
import com.ezra.loanservice.dto.LoanSummaryResponse;
import com.ezra.loanservice.enums.LoanState;
import com.ezra.loanservice.models.Loan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillingServiceImpl implements BillingService {
    private final LoanRepository loanRepository;

    @Transactional(readOnly = true)
    public LoanSummaryResponse getCustomerSummary(UUID customerId) {
        List<Loan> loans = loanRepository.findByCustomerId(customerId);

        long activeLoans = loans.stream().filter(l -> l.getState() == LoanState.OPEN).count();
        long overdueLoans = loans.stream().filter(l -> l.getState() == LoanState.OVERDUE).count();
        BigDecimal totalDisbursed = loans.stream()
                .map(Loan::getPrincipalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalOutstanding = loans.stream()
                .filter(l -> !l.getState().equals(LoanState.CLOSED) && !l.getState().equals(LoanState.CANCELLED))
                .map(Loan::getOutstandingBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return LoanSummaryResponse.builder()
                .totalLoans(loans.size())
                .activeLoans(activeLoans)
                .overdueLoans(overdueLoans)
                .totalDisbursed(totalDisbursed)
                .totalOutstanding(totalOutstanding)
                .build();
    }
}
