package com.ezra.loanservice.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

/**
 * Fallback factory for CustomerClient. Invoked when the circuit breaker is open
 * or when customer-service is unreachable. Logs the cause and throws a descriptive
 * runtime exception so the caller gets a meaningful error instead of a raw FeignException.
 */
@Component
@Slf4j
public class CustomerClientFallbackFactory implements FallbackFactory<CustomerClient> {

    @Override
    public CustomerClient create(Throwable cause) {
        return new CustomerClient() {
            @Override
            public Map<String, Object> getCustomer(UUID customerId) {
                log.error("Customer service unavailable. Failed to fetch customer {}: {}", customerId, cause.getMessage());
                throw new ServiceUnavailableException("Customer service is currently unavailable. Please try again later.");
            }

            @Override
            public Map<String, Object> checkLoanLimitPost(UUID customerId, Map<String, Object> request) {
                log.error("Customer service unavailable. Failed to check loan limit for {}: {}", customerId, cause.getMessage());
                throw new ServiceUnavailableException("Customer service is currently unavailable. Please try again later.");
            }
        };
    }
}
