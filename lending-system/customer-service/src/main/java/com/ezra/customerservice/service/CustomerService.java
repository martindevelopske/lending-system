package com.ezra.customerservice.service;

import com.ezra.customerservice.dto.CustomerCreateRequest;
import com.ezra.customerservice.dto.CustomerResponse;
import com.ezra.customerservice.dto.CustomerUpdateRequest;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface CustomerService {
    CustomerResponse getCustomer(UUID id);

    List<CustomerResponse> getAllCustomers();

    CustomerResponse updateCustomer(UUID id, CustomerUpdateRequest request);

    CustomerResponse createCustomer(@Valid CustomerCreateRequest request);
}
