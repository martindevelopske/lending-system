package com.ezra.productservice.mapper;

import com.ezra.productservice.dtos.ProductDto;
import com.ezra.productservice.models.Fee;
import com.ezra.productservice.models.Product;
import org.springframework.beans.factory.annotation.Autowired;

public class ProductMapperDecorator implements ProductMapper {
    @Autowired
    private ProductMapper delegate;

    @Override
    public ProductDto toDto(Product product) {
        return delegate.toDto(product);
    }

    @Override
    public Product toModel(ProductDto productDto) {
        Product product = delegate.toModel(productDto);

        if (product.getFees() != null) {
            for (Fee fee : product.getFees()) {
                fee.setProduct(product);
            }
        }
        return product;
    }
}
