package com.ezra.loanservice.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

/**
 * Fallback factory for ProductClient. Invoked when the circuit breaker is open
 * or when product-service is unreachable. Logs the cause and throws a descriptive
 * runtime exception so the caller gets a meaningful error instead of a raw FeignException.
 */
@Component
@Slf4j
public class ProductClientFallbackFactory implements FallbackFactory<ProductClient> {

    @Override
    public ProductClient create(Throwable cause) {
        return new ProductClient() {
            @Override
            public Map<String, Object> getProduct(UUID productId) {
                log.error("Product service unavailable. Failed to fetch product {}: {}", productId, cause.getMessage());
                throw new ServiceUnavailableException("Product service is currently unavailable. Please try again later.");
            }
        };
    }
}
