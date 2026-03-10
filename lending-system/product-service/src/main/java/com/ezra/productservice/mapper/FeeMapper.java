package com.ezra.productservice.mapper;

import com.ezra.productservice.dtos.FeeDto;
import com.ezra.productservice.models.Fee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FeeMapper {

    @Mapping(source = "product.id", target = "productId")
    FeeDto toDto(Fee fee);

    @Mapping(target = "product", ignore = true)
    Fee toModel(FeeDto feeDto);
}
