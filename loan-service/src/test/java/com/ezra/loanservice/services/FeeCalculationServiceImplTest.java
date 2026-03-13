package com.ezra.loanservice.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class FeeCalculationServiceImplTest {

    private FeeCalculationServiceImpl feeCalculationService;

    @BeforeEach
    void setUp() {
        feeCalculationService = new FeeCalculationServiceImpl();
    }

    @Test
    void calculateServiceFee_withPercentageFee_returnsCorrectAmount() {
        BigDecimal principal = new BigDecimal("10000");
        List<Map<String, Object>> fees = List.of(
                Map.of("feeType", "SERVICE", "calculationMethod", "PERCENTAGE", "amount", "2.5")
        );

        BigDecimal result = feeCalculationService.calculateServiceFee(principal, fees);

        assertThat(result).isEqualByComparingTo(new BigDecimal("250.0000"));
    }

    @Test
    void calculateServiceFee_withFixedFee_returnsFixedAmount() {
        BigDecimal principal = new BigDecimal("10000");
        List<Map<String, Object>> fees = List.of(
                Map.of("feeType", "SERVICE", "calculationMethod", "FIXED", "amount", "500")
        );

        BigDecimal result = feeCalculationService.calculateServiceFee(principal, fees);

        assertThat(result).isEqualByComparingTo(new BigDecimal("500"));
    }

    @Test
    void calculateServiceFee_withMultipleFees_sumsThem() {
        BigDecimal principal = new BigDecimal("10000");
        List<Map<String, Object>> fees = List.of(
                Map.of("feeType", "SERVICE", "calculationMethod", "PERCENTAGE", "amount", "1"),
                Map.of("feeType", "SERVICE", "calculationMethod", "FIXED", "amount", "200")
        );

        BigDecimal result = feeCalculationService.calculateServiceFee(principal, fees);

        assertThat(result).isEqualByComparingTo(new BigDecimal("300"));
    }

    @Test
    void calculateServiceFee_ignoresNonServiceFees() {
        BigDecimal principal = new BigDecimal("10000");
        List<Map<String, Object>> fees = List.of(
                Map.of("feeType", "DAILY", "calculationMethod", "FIXED", "amount", "50"),
                Map.of("feeType", "SERVICE", "calculationMethod", "FIXED", "amount", "100")
        );

        BigDecimal result = feeCalculationService.calculateServiceFee(principal, fees);

        assertThat(result).isEqualByComparingTo(new BigDecimal("100"));
    }

    @Test
    void calculateServiceFee_withNoServiceFees_returnsZero() {
        BigDecimal principal = new BigDecimal("10000");
        List<Map<String, Object>> fees = List.of(
                Map.of("feeType", "DAILY", "calculationMethod", "FIXED", "amount", "50")
        );

        BigDecimal result = feeCalculationService.calculateServiceFee(principal, fees);

        assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void calculateDailyFee_withPercentageFee_returnsCorrectAmount() {
        BigDecimal principal = new BigDecimal("10000");
        List<Map<String, Object>> fees = List.of(
                Map.of("feeType", "DAILY", "calculationMethod", "PERCENTAGE", "amount", "0.1")
        );

        BigDecimal result = feeCalculationService.calculateDailyFee(principal, fees);

        assertThat(result).isEqualByComparingTo(new BigDecimal("10.0000"));
    }

    @Test
    void calculateDailyFee_withFixedFee_returnsFixedAmount() {
        BigDecimal principal = new BigDecimal("10000");
        List<Map<String, Object>> fees = List.of(
                Map.of("feeType", "DAILY", "calculationMethod", "FIXED", "amount", "25")
        );

        BigDecimal result = feeCalculationService.calculateDailyFee(principal, fees);

        assertThat(result).isEqualByComparingTo(new BigDecimal("25"));
    }

    @Test
    void calculateDailyFee_ignoresServiceFees() {
        BigDecimal principal = new BigDecimal("10000");
        List<Map<String, Object>> fees = List.of(
                Map.of("feeType", "SERVICE", "calculationMethod", "FIXED", "amount", "500")
        );

        BigDecimal result = feeCalculationService.calculateDailyFee(principal, fees);

        assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
