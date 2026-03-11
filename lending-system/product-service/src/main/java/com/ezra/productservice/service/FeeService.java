package com.ezra.productservice.service;

import com.ezra.productservice.dtos.FeeCreationRequest;
import com.ezra.productservice.dtos.FeeDto;
import jakarta.validation.Valid;

import java.util.UUID;

public interface FeeService {
    FeeDto addFee(UUID productId, @Valid FeeCreationRequest request);

    FeeDto updateFee(UUID productId, UUID feeId, @Valid FeeCreationRequest request);

    void removeFee(UUID productId, UUID feeId);
}
