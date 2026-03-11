package com.ezra.customerservice.service;

import com.ezra.customerservice.dto.CustomerCreateRequest;
import com.ezra.customerservice.dto.CustomerResponse;
import com.ezra.customerservice.dto.CustomerUpdateRequest;
import com.ezra.customerservice.exception.CustomerNotFoundException;
import com.ezra.customerservice.exception.DuplicateCustomerException;
import com.ezra.customerservice.mapper.CustomerMapper;
import com.ezra.customerservice.models.Customer;
import com.ezra.customerservice.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;

    private final LoanLimitService loanLimitService;
    private final CustomerMapper customerMapper;

    @Override
    public CustomerResponse getCustomer(UUID id) {
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + id));
        return customerMapper.toResponse(customer);
    }

    @Override
    public List<CustomerResponse> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return customers.stream().map(customerMapper::toResponse).toList();
    }

    @Override
    public CustomerResponse updateCustomer(UUID id, CustomerUpdateRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + id));

        customerMapper.updateEntity(customer, request);
        customer = customerRepository.save(customer);

        log.info("Updated customer: {}", id);

        //publish customer updated event

        return customerMapper.toResponse(customer);
    }

    @Override
    public CustomerResponse createCustomer(CustomerCreateRequest request) {
        validateUniqueness(request);

        Customer customer = customerMapper.toEntity(request);
        customer = customerRepository.save(customer);

        log.info("Created customer: {} {} ({})", customer.getFirstName(), customer.getLastName(), customer.getId());

        //publish customer created event

        return customerMapper.toResponse(customer);
    }

    private void validateUniqueness(CustomerCreateRequest request) {
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateCustomerException("Customer with email '" + request.getEmail() + "' already exists");
        }
        if (customerRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new DuplicateCustomerException("Customer with phone '" + request.getPhoneNumber() + "' already exists");
        }
        if (customerRepository.existsByNationalId(request.getNationalId())) {
            throw new DuplicateCustomerException("Customer with national ID '" + request.getNationalId() + "' already exists");
        }
    }
}
