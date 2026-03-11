package com.ezra.customerservice.service;

import com.ezra.customerservice.dto.LoanLimitCheckRequest;
import com.ezra.customerservice.dto.LoanLimitCheckResponse;
import com.ezra.customerservice.dto.LoanLimitRequest;
import com.ezra.customerservice.dto.LoanLimitResponse;
import com.ezra.customerservice.exception.CustomerNotFoundException;
import com.ezra.customerservice.exception.LoanLimitExceededException;
import com.ezra.customerservice.mapper.LoanLimitMapper;
import com.ezra.customerservice.models.Customer;
import com.ezra.customerservice.models.LoanLimit;
import com.ezra.customerservice.repository.CustomerRepository;
import com.ezra.customerservice.repository.LoanLimitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanLimitServiceImpl implements LoanLimitService {
    private final CustomerRepository customerRepository;
    private final LoanLimitRepository loanLimitRepository;
    private final LoanLimitMapper loanLimitMapper;

    @Override
    public LoanLimitResponse setLoanLimit(UUID customerId, LoanLimitRequest request) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + customerId));
        LoanLimit loanLimit = loanLimitRepository.findByCustomerId(customerId).orElse(LoanLimit.builder().customer(customer).build());

        loanLimit.setMaxLoanAmount(request.getMaxLoanAmount());
        loanLimit.setAvailableAmount(request.getMaxLoanAmount());
        loanLimit = loanLimitRepository.save(loanLimit);

        log.info("Set loan limit for customer {}: {}", customerId, request.getMaxLoanAmount());
        return loanLimitMapper.toLoanLimitResponse(loanLimit);
    }

    @Override
    public LoanLimitResponse getLoanLimit(UUID customerId) {
        LoanLimit loanLimit = loanLimitRepository.findByCustomerId(customerId).orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + customerId));
        return loanLimitMapper.toLoanLimitResponse(loanLimit);
    }

    @Override
    public LoanLimitCheckResponse checkLoanLimit(UUID customerId, LoanLimitCheckRequest request) {
        LoanLimit loanLimit = loanLimitRepository.findByCustomerId(customerId).orElse(null);

        if (loanLimit == null) {
            return LoanLimitCheckResponse.builder()
                    .eligible(false)
                    .requestedAmount(request.getRequestedAmount())
                    .availableAmount(BigDecimal.ZERO)
                    .message("No loan limit set for this customer")
                    .build();
        }

        boolean eligible = loanLimit.getAvailableAmount().compareTo(request.getRequestedAmount()) >= 0;
        return LoanLimitCheckResponse.builder()
                .eligible(eligible)
                .requestedAmount(request.getRequestedAmount())
                .availableAmount(loanLimit.getAvailableAmount())
                .message(eligible ? "Customer is eligible" : "Insufficient loan limit")
                .build();
    }

    @Transactional
    public void decreaseAvailableAmount(UUID customerId, BigDecimal amount) {
        LoanLimit loanLimit = loanLimitRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Loan limit not found for customer: " + customerId));

        if (loanLimit.getAvailableAmount().compareTo(amount) < 0) {
            throw new LoanLimitExceededException("Insufficient available limit for customer: " + customerId);
        }

        loanLimit.setAvailableAmount(loanLimit.getAvailableAmount().subtract(amount));
        loanLimitRepository.save(loanLimit);
        log.info("Decreased available amount for customer {} by {}", customerId, amount);
    }

    @Transactional
    public void increaseAvailableAmount(UUID customerId, BigDecimal amount) {
        LoanLimit loanLimit = loanLimitRepository.findByCustomerId(customerId).orElse(null);

        if (loanLimit == null) {
            log.warn("No loan limit found for customer {}, skipping increase", customerId);
            return;
        }

        BigDecimal newAmount = loanLimit.getAvailableAmount().add(amount);
        if (newAmount.compareTo(loanLimit.getMaxLoanAmount()) > 0) {
            newAmount = loanLimit.getMaxLoanAmount();
        }

        loanLimit.setAvailableAmount(newAmount);
        loanLimitRepository.save(loanLimit);
        log.info("Increased available amount for customer {} by {}", customerId, amount);
    }

}
