package com.ezra.customerservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerUpdateRequest {

    @Size(min = 1, max = 100, message = "First name must be 1-100 characters")
    private String firstName;

    @Size(min = 1, max = 100, message = "Last name must be 1-100 characters")
    private String lastName;

    @Email(message = "Valid email is required")
    private String email;

    @Size(min = 1, max = 20, message = "Phone number must be 1-20 characters")
    private String phoneNumber;

    private LocalDate dateOfBirth;
}
