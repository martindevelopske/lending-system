package com.ezra.productservice.dtos;

import com.ezra.productservice.enums.LoanStructure;
import com.ezra.productservice.enums.TenureType;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCreationRequest {
    private String name;

    private String description;

    private TenureType tenureType;

    private BigDecimal tenureValue;

    private LoanStructure loanStructure;

    private Integer interestRate;

    private BigDecimal minimumAmount;

    private BigDecimal maximumAmount;

    private Boolean isActive;

    private List<FeeDto> fees;
}
