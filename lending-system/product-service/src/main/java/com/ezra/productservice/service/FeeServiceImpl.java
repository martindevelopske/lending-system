package com.ezra.productservice.service;

import com.ezra.productservice.dtos.FeeCreationRequest;
import com.ezra.productservice.dtos.FeeDto;
import com.ezra.productservice.exception.FeeNotFoundException;
import com.ezra.productservice.exception.ProductNotFoundException;
import com.ezra.productservice.mapper.ProductMapper;
import com.ezra.productservice.models.Fee;
import com.ezra.productservice.models.Product;
import com.ezra.productservice.repository.FeeRepository;
import com.ezra.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeeServiceImpl implements FeeService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final FeeRepository feeRepository;

    @Override
    public FeeDto addFee(UUID productId, FeeCreationRequest request) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("Product not found: " + productId));
        Fee fee = productMapper.toFeeEntity(request);
        product.addFee(fee);
        productRepository.save(product);

        log.info("Added fee '{}' to product: ({})", fee.getName(), product.getId());
        return productMapper.toFeeDto(fee);


    }

    @Override
    public FeeDto updateFee(UUID productId, UUID feeId, FeeCreationRequest request) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("Product not found: " + productId));
        Fee fee= feeRepository.findById(feeId).orElseThrow(() -> new FeeNotFoundException("Fee not found: " + feeId));

        fee.setName(request.getName());
        fee.setFeeType(request.getFeeType());
        fee.setCalculationMethod(request.getCalculationMethod());
        fee.setAmount(request.getAmount());
        fee.setDaysAfterDue(request.getDaysAfterDue());
        fee=feeRepository.save(fee);


        log.info("Updated fee '{}' to product: ({})", fee.getName(), product.getId());
        return productMapper.toFeeDto(fee);
    }

    @Override
    public void removeFee(UUID productId, UUID feeId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("Product not found: " + productId));
        Fee fee = feeRepository.findById(feeId).orElseThrow(() -> new FeeNotFoundException("Fee not found: " + feeId));
        product.removeFee(fee);
        productRepository.save(product);
        log.info("Removed fee '{}' from product: ({})", fee.getName(), product.getId());
    }
}
