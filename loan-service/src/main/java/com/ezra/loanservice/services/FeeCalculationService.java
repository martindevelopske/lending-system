package com.ezra.loanservice.services;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface FeeCalculationService {
    BigDecimal calculateServiceFee(@NotNull @DecimalMin(value = "0.01") BigDecimal amount, List<Map<String, Object>> fees);
}
