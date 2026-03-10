package com.ezra.productservice.mapper;
import com.ezra.productservice.dtos.ProductDto;
import com.ezra.productservice.models.Product;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

@Mapper(componentModel="spring", uses = {FeeMapper.class})
@DecoratedWith(ProductMapperDecorator.class)
public interface ProductMapper {
    ProductDto toDto(Product product);
    Product toModel(ProductDto productDto);

}
