package com.ezra.loanservice.exceptions;

public class InsufficientLoanLimitException extends RuntimeException {
    public InsufficientLoanLimitException(String message) {
        super(message);
    }
}
