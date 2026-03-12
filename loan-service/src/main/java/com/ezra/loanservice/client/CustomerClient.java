package com.ezra.loanservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "customer-service")
public interface CustomerClient {
    @GetMapping("/api/v1/customers/{id}")
    Map<String, Object> getCustomer(@PathVariable("id") UUID customerId);

    default Map<String, Object> checkLoanLimit(UUID customerId, BigDecimal amount) {

        return checkLoanLimitPost(customerId, Map.of("requestedAmount", amount));
    }

    @PostMapping("/api/v1/customers/{id}/loan-limit/check")
    Map<String, Object> checkLoanLimitPost(@PathVariable("id") UUID customerId, @RequestBody Map<String, Object> request);
}
