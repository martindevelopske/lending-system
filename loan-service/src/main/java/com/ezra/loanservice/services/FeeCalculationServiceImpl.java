package com.ezra.loanservice.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

/**
 * Calculates fees based on product fee configuration retrieved from the product-service.
 * Supports two calculation methods: PERCENTAGE (of principal/outstanding) and FIXED amount.
 * All monetary calculations use 4 decimal precision with HALF_UP rounding.
 */
@Service
@Slf4j
public class FeeCalculationServiceImpl implements FeeCalculationService {

    /**
     * Calculates the total one-time service fee applied at loan disbursement.
     * Only processes fees with feeType=SERVICE; DAILY and LATE fees are ignored.
     *
     * @param principalAmount the loan principal to calculate percentage fees against
     * @param fees            list of fee configurations from the product-service
     * @return total service fee (ZERO if no SERVICE fees are configured)
     */
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

    /**
     * Calculates the daily fee to accrue on outstanding loan balance.
     * Only processes fees with feeType=DAILY; SERVICE and LATE fees are ignored.
     * Called by DailyFeeAccrualJob for each active loan.
     *
     * @param principalAmount the current outstanding balance
     * @param fees            list of fee configurations from the product-service
     * @return daily fee amount to accrue
     */
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
