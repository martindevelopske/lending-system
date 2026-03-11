package com.ezra.productservice.mapper;

import com.ezra.productservice.dtos.FeeCreationRequest;
import com.ezra.productservice.dtos.FeeDto;
import com.ezra.productservice.models.Fee;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
@DecoratedWith(FeeMapperDecorator.class)
public interface FeeMapper {
    Fee toFeeEntity(FeeCreationRequest request);

    FeeDto toFeeDto(Fee fee);
}
