package com.ezra.productservice.service;

import com.ezra.productservice.dtos.ProductCreationRequest;
import com.ezra.productservice.dtos.ProductDto;
import com.ezra.productservice.dtos.ProductUpdateRequest;
import com.ezra.productservice.exception.DuplicateProductException;
import com.ezra.productservice.exception.ProductNotFoundException;
import com.ezra.productservice.mapper.ProductMapper;
import com.ezra.productservice.models.Product;
import com.ezra.productservice.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductDto getProductById(UUID id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product not found: " + id));
        return productMapper.toDto(product);
    }

    @Override
    public List<ProductDto> getAllProducts() {
        // todo maybe paginate
        return productRepository.findAll().stream().map(productMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public ProductDto createProduct(ProductCreationRequest productCreationRequest) {
        if (productRepository.existsByName(productCreationRequest.getName())) {
            throw new DuplicateProductException("Product with name '" + productCreationRequest.getName() + "'already exists");
        }

        Product product = productMapper.toNewProduct(productCreationRequest);
        product = productRepository.save(product);

        log.info("Created product: {} ({})", product.getName(), product.getId());

        //publish product created event

        return productMapper.toDto(product);
    }

    @Transactional
    public ProductDto updateProduct(UUID id, ProductUpdateRequest productUpdateRequest) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product not found: " + id));
        productMapper.updateEntity(product, productUpdateRequest);
        product = productRepository.save(product);
        log.info("Updated product: {} ({})", product.getName(), product.getId());

        //publish product updated event
        return productMapper.toDto(product);
    }

    @Override
    public void deleteProduct(UUID id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product not found: " + id));
        productRepository.delete(product);
        log.info("Deleted product: {} ({})", product.getName(), product.getId());
    }

}
