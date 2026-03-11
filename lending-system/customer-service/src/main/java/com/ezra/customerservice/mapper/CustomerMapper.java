package com.ezra.customerservice.mapper;

import com.ezra.customerservice.dto.CustomerCreateRequest;
import com.ezra.customerservice.dto.CustomerResponse;
import com.ezra.customerservice.dto.CustomerUpdateRequest;
import com.ezra.customerservice.models.Customer;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
@DecoratedWith(CustomerMapperDecorator.class)
public interface CustomerMapper {
    Customer toEntity(CustomerCreateRequest request);

    CustomerResponse toResponse(Customer customer);

    void updateEntity(@MappingTarget Customer customer, CustomerUpdateRequest request);
}
