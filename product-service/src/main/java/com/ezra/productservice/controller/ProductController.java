package com.ezra.productservice.controller;

import com.ezra.productservice.dtos.FeeCreationRequest;
import com.ezra.productservice.dtos.FeeDto;
import com.ezra.productservice.dtos.ProductCreationRequest;
import com.ezra.productservice.dtos.ProductDto;
import com.ezra.productservice.service.FeeService;
import com.ezra.productservice.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final FeeService feeService;

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }
    @GetMapping()
    public ResponseEntity<List<ProductDto>> getProductAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductCreationRequest productCreationRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(productCreationRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{productId}/fees")
    public ResponseEntity<FeeDto> addFee(@PathVariable UUID productId,
                                         @Valid @RequestBody FeeCreationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(feeService.addFee(productId, request));
    }

    @PutMapping("/{productId}/fees/{feeId}")
    public ResponseEntity<FeeDto> updateFee(@PathVariable UUID productId,
                                                 @PathVariable UUID feeId,
                                                 @Valid @RequestBody FeeCreationRequest request) {
        return ResponseEntity.ok(feeService.updateFee(productId, feeId, request));
    }

    @DeleteMapping("/{productId}/fees/{feeId}")
    public ResponseEntity<Void> removeFee(@PathVariable UUID productId, @PathVariable UUID feeId) {
        feeService.removeFee(productId, feeId);
        return ResponseEntity.noContent().build();
    }
}
