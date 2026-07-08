package com.ezra.loanservice.client;

/**
 * Thrown when a downstream service (product-service, customer-service) is unreachable
 * or when the circuit breaker is open. Mapped to HTTP 503 Service Unavailable.
 */
public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(String message) {
        super(message);
    }
}
