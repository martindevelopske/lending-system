package com.ezra.productservice.service;

import com.ezra.productservice.dtos.ProductCreationRequest;
import com.ezra.productservice.dtos.ProductDto;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    ProductDto getProductById(UUID id);

    List<ProductDto> getAllProducts();

    ProductDto createProduct(@Valid ProductCreationRequest productCreationRequest);

    void deleteProduct(UUID id);
}
