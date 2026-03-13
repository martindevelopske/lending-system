package com.ezra.customerservice.service;

import com.ezra.customerservice.dto.CustomerCreateRequest;
import com.ezra.customerservice.dto.CustomerResponse;
import com.ezra.customerservice.dto.CustomerUpdateRequest;
import com.ezra.customerservice.event.CustomerEventPublisher;
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

/**
 * Service managing customer registration and profile updates.
 * Enforces uniqueness on email, phone number, and national ID.
 * Publishes customer lifecycle events to Kafka for downstream services.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;

    private final LoanLimitService loanLimitService;
    private final CustomerMapper customerMapper;
    private final CustomerEventPublisher customerEventPublisher;

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
        customerEventPublisher.publishCustomerUpdatedEvent(customer);
        log.info("Published customer updated event for customer: {}", id);

        log.info("Updated customer: {} {} ({})", customer.getFirstName(), customer.getLastName(), customer.getId());
        return customerMapper.toResponse(customer);
    }

    @Override
    public CustomerResponse createCustomer(CustomerCreateRequest request) {
        validateUniqueness(request);

        Customer customer = customerMapper.toEntity(request);
        customer = customerRepository.save(customer);

        log.info("Created customer: {} {} ({})", customer.getFirstName(), customer.getLastName(), customer.getId());

        //publish customer created event
        customerEventPublisher.publishCustomerCreatedEvent(customer);
        log.info("Published customer created event for customer: {}", customer.getId());

        log.info("Created customer: {} {} ({})", customer.getFirstName(), customer.getLastName(), customer.getId());

        return customerMapper.toResponse(customer);
    }

    /** Validates that email, phone number, and national ID are not already registered. */
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
