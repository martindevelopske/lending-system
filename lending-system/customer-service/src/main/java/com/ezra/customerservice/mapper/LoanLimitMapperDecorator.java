package com.ezra.customerservice.mapper;

import com.ezra.customerservice.dto.LoanLimitResponse;
import com.ezra.customerservice.models.LoanLimit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoanLimitMapperDecorator implements LoanLimitMapper {
    @Autowired
    private LoanLimitMapper delegate;

    @Override
    public LoanLimitResponse toLoanLimitResponse(LoanLimit limit) {
        return LoanLimitResponse.builder()
                .id(limit.getId())
                .maxLoanAmount(limit.getMaxLoanAmount())
                .availableAmount(limit.getAvailableAmount())
                .build();
    }
}
