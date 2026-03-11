package com.ezra.customerservice.exception;

public class LoanLimitExceededException extends RuntimeException {
    public LoanLimitExceededException(String message) {
        super(message);
    }
}
