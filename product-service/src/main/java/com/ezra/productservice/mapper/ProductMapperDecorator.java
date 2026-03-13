package com.ezra.productservice.mapper;

import com.ezra.productservice.dtos.*;
import com.ezra.productservice.models.Fee;
import com.ezra.productservice.models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class ProductMapperDecorator implements ProductMapper {
    @Autowired
    @Qualifier("productMapperImpl")
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

    @Override
    public Product toNewProduct(ProductCreationRequest request) {
        return Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .minimumAmount(request.getMinimumAmount())
                .maximumAmount(request.getMaximumAmount())
                .interestRate(request.getInterestRate())
                .tenureValue(request.getTenureValue())
                .tenureType(request.getTenureType())
                .loanStructure(request.getLoanStructure())
                .isActive(true)
                .build();
    }

    @Override
    public Product updateEntity(Product product, ProductUpdateRequest request) {
        if (request.getName() != null) product.setName(request.getName());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getMinAmount() != null) product.setMinimumAmount(request.getMinAmount());
        if (request.getMaxAmount() != null) product.setMaximumAmount(request.getMaxAmount());
        if (request.getInterestRate() != null) product.setInterestRate(request.getInterestRate());
        if (request.getTenureValue() != null) product.setTenureValue(request.getTenureValue());
        if (request.getTenureType() != null) product.setTenureType(request.getTenureType());
        if (request.getLoanStructure() != null) product.setLoanStructure(request.getLoanStructure());
        if (request.getActive() != null) product.setIsActive(request.getActive());
        return product;
    }
}
