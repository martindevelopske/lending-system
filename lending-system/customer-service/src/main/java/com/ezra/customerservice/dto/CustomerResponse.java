package com.ezra.customerservice.dto;

import com.ezra.customerservice.enums.CustomerStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerResponse {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String nationalId;
    private LocalDate dateOfBirth;
    private CustomerStatus status;
    private LoanLimitResponse loanLimit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}