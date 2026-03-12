package com.ezra.productservice.exception;

public class FeeNotFoundException extends RuntimeException {
    public FeeNotFoundException(String message) {
        super(message);
    }
}
