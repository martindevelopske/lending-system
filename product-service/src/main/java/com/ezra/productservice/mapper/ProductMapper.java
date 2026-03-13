package com.ezra.productservice.mapper;
import com.ezra.productservice.dtos.*;
import com.ezra.productservice.models.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel="spring")
public interface ProductMapper {
    ProductDto toDto(Product product);
    Product toModel(ProductDto productDto);

    Product toNewProduct(ProductCreationRequest productDto);

    Product updateEntity(@MappingTarget Product product, ProductUpdateRequest request);
}
