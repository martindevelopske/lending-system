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

    // --- Late fee calculation tests ---

    @Test
    void calculateLateFee_withFixedFee_afterThreshold_returnsAmount() {
        BigDecimal outstanding = new BigDecimal("5000");
        List<Map<String, Object>> fees = List.of(
                Map.of("feeType", "LATE", "calculationMethod", "FIXED", "amount", "50", "daysAfterDue", 3)
        );

        BigDecimal result = feeCalculationService.calculateLateFee(outstanding, fees, 5);

        assertThat(result).isEqualByComparingTo(new BigDecimal("50"));
    }

    @Test
    void calculateLateFee_withFixedFee_beforeThreshold_returnsZero() {
        BigDecimal outstanding = new BigDecimal("5000");
        List<Map<String, Object>> fees = List.of(
                Map.of("feeType", "LATE", "calculationMethod", "FIXED", "amount", "50", "daysAfterDue", 3)
        );

        BigDecimal result = feeCalculationService.calculateLateFee(outstanding, fees, 2);

        assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void calculateLateFee_withFixedFee_exactlyAtThreshold_returnsAmount() {
        BigDecimal outstanding = new BigDecimal("5000");
        List<Map<String, Object>> fees = List.of(
                Map.of("feeType", "LATE", "calculationMethod", "FIXED", "amount", "100", "daysAfterDue", 3)
        );

        BigDecimal result = feeCalculationService.calculateLateFee(outstanding, fees, 3);

        assertThat(result).isEqualByComparingTo(new BigDecimal("100"));
    }

    @Test
    void calculateLateFee_withPercentageFee_returnsCorrectAmount() {
        BigDecimal outstanding = new BigDecimal("10000");
        List<Map<String, Object>> fees = List.of(
                Map.of("feeType", "LATE", "calculationMethod", "PERCENTAGE", "amount", "5", "daysAfterDue", 0)
        );

        BigDecimal result = feeCalculationService.calculateLateFee(outstanding, fees, 1);

        assertThat(result).isEqualByComparingTo(new BigDecimal("500.0000"));
    }

    @Test
    void calculateLateFee_withNullDaysAfterDue_defaultsToZero() {
        BigDecimal outstanding = new BigDecimal("10000");
        java.util.Map<String, Object> feeMap = new java.util.HashMap<>();
        feeMap.put("feeType", "LATE");
        feeMap.put("calculationMethod", "FIXED");
        feeMap.put("amount", "75");
        feeMap.put("daysAfterDue", null);
        List<Map<String, Object>> fees = List.of(feeMap);

        BigDecimal result = feeCalculationService.calculateLateFee(outstanding, fees, 1);

        assertThat(result).isEqualByComparingTo(new BigDecimal("75"));
    }

    @Test
    void calculateLateFee_ignoresServiceAndDailyFees() {
        BigDecimal outstanding = new BigDecimal("10000");
        List<Map<String, Object>> fees = List.of(
                Map.of("feeType", "SERVICE", "calculationMethod", "FIXED", "amount", "500"),
                Map.of("feeType", "DAILY", "calculationMethod", "FIXED", "amount", "25")
        );

        BigDecimal result = feeCalculationService.calculateLateFee(outstanding, fees, 10);

        assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void calculateLateFee_withMultipleLateFees_sumsApplicableOnes() {
        BigDecimal outstanding = new BigDecimal("10000");
        List<Map<String, Object>> fees = List.of(
                Map.of("feeType", "LATE", "calculationMethod", "FIXED", "amount", "50", "daysAfterDue", 3),
                Map.of("feeType", "LATE", "calculationMethod", "PERCENTAGE", "amount", "2", "daysAfterDue", 7)
        );

        // At 5 days overdue: only the first fee (3 days threshold) applies
        BigDecimal result5 = feeCalculationService.calculateLateFee(outstanding, fees, 5);
        assertThat(result5).isEqualByComparingTo(new BigDecimal("50"));

        // At 10 days overdue: both fees apply (50 fixed + 2% of 10000 = 200)
        BigDecimal result10 = feeCalculationService.calculateLateFee(outstanding, fees, 10);
        assertThat(result10).isEqualByComparingTo(new BigDecimal("250.0000"));
    }
}
