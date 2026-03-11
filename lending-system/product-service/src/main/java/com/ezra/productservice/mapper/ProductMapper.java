package com.ezra.productservice.mapper;
import com.ezra.productservice.dtos.*;
import com.ezra.productservice.models.Fee;
import com.ezra.productservice.models.Product;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

@Mapper(componentModel="spring", uses = {FeeMapper.class})
@DecoratedWith(ProductMapperDecorator.class)
public interface ProductMapper {
    ProductDto toDto(Product product);
    Product toModel(ProductDto productDto);

    Product toNewProduct(ProductCreationRequest productDto);

    void updateEntity(Product product, ProductUpdateRequest request);

    Fee toFeeEntity(FeeCreationRequest request);

    FeeDto toFeeDto(Fee request);
}
