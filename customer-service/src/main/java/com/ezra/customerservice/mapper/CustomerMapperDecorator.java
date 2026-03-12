package com.ezra.customerservice.mapper;

import com.ezra.customerservice.dto.CustomerCreateRequest;
import com.ezra.customerservice.dto.CustomerResponse;
import com.ezra.customerservice.dto.CustomerUpdateRequest;
import com.ezra.customerservice.enums.CustomerStatus;
import com.ezra.customerservice.models.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapperDecorator implements CustomerMapper {
    @Autowired
    private CustomerMapper delegate;

    @Autowired
    private LoanLimitMapper loanLimitMapper;

    @Override
    public Customer toEntity(CustomerCreateRequest request) {
        return Customer.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .nationalId(request.getNationalId())
                .dateOfBirth(request.getDateOfBirth())
                .status(CustomerStatus.ACTIVE)
                .build();
    }

    @Override
    public CustomerResponse toResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .nationalId(customer.getNationalId())
                .dateOfBirth(customer.getDateOfBirth())
                .status(customer.getStatus())
                .loanLimit(customer.getLoanLimit() != null
                        ? loanLimitMapper.toLoanLimitResponse(customer.getLoanLimit())
                        : null)
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();
    }

    @Override
    public void updateEntity(Customer customer, CustomerUpdateRequest request) {
        if (request.getFirstName() != null) customer.setFirstName(request.getFirstName());
        if (request.getLastName() != null) customer.setLastName(request.getLastName());
        if (request.getEmail() != null) customer.setEmail(request.getEmail());
        if (request.getPhoneNumber() != null) customer.setPhoneNumber(request.getPhoneNumber());
        if (request.getDateOfBirth() != null) customer.setDateOfBirth(request.getDateOfBirth());
    }
}
