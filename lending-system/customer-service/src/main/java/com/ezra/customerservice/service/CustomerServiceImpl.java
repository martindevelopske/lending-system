package com.ezra.customerservice.service;

import com.ezra.customerservice.dto.CustomerCreateRequest;
import com.ezra.customerservice.dto.CustomerResponse;
import com.ezra.customerservice.dto.CustomerUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService{
    @Override
    public CustomerResponse getCustomer(UUID id) {
        return null;
    }

    @Override
    public List<CustomerResponse> getAllCustomers() {
        return List.of();
    }

    @Override
    public CustomerResponse updateCustomer(UUID id, CustomerUpdateRequest request) {
        return null;
    }

    @Override
    public CustomerResponse createCustomer(CustomerCreateRequest request) {
        return null;
    }
}
