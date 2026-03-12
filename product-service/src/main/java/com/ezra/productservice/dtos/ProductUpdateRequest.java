package com.ezra.productservice.dtos;

import com.ezra.productservice.enums.LoanStructure;
import com.ezra.productservice.enums.TenureType;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductUpdateRequest {
    private String name;
    private String description;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private Integer interestRate;
    private BigDecimal tenureValue;
    private TenureType tenureType;
    private LoanStructure loanStructure;
    private Boolean active;
}
