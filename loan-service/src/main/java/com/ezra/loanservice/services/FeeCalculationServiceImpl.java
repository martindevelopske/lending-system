package com.ezra.loanservice.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class FeeCalculationServiceImpl implements FeeCalculationService{

    @Override
    public BigDecimal calculateServiceFee(BigDecimal principalAmount, List<Map<String, Object>> fees) {
        BigDecimal totalServiceFee = BigDecimal.ZERO;

        for (Map<String, Object> fee : fees) {
            if ("SERVICE".equals(fee.get("feeType"))) {
                BigDecimal feeAmount = new BigDecimal(fee.get("amount").toString());
                String method = (String) fee.get("calculationMethod");

                if ("PERCENTAGE".equals(method)) {
                    totalServiceFee = totalServiceFee.add(
                            principalAmount.multiply(feeAmount).divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));
                } else {
                    totalServiceFee = totalServiceFee.add(feeAmount);
                }
            }
        }

        log.debug("Calculated service fee: {} on principal: {}", totalServiceFee, principalAmount);
        return totalServiceFee;
    }

    @Override
    public BigDecimal calculateDailyFee(BigDecimal principalAmount, List<Map<String, Object>> fees) {
        BigDecimal totalDailyFee = BigDecimal.ZERO;

        for (Map<String, Object> fee : fees) {
            if ("DAILY".equals(fee.get("feeType"))) {
                BigDecimal feeAmount = new BigDecimal(fee.get("amount").toString());
                String method = (String) fee.get("calculationMethod");

                if ("PERCENTAGE".equals(method)) {
                    totalDailyFee = totalDailyFee.add(
                            principalAmount.multiply(feeAmount).divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));
                } else {
                    totalDailyFee = totalDailyFee.add(feeAmount);
                }
            }
        }

        return totalDailyFee;
    }
}
