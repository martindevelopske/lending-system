package com.ezra.customerservice.repository;

import com.ezra.customerservice.models.Customer;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    boolean existsByEmail(@NotBlank @Email(message = "Valid email is required") String email);

    boolean existsByPhoneNumber(@NotBlank(message = "Phone number is required") String phoneNumber);

    boolean existsByNationalId(@NotBlank(message = "National ID is required") String nationalId);
}
