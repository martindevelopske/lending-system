package com.ezra.productservice.dtos;

import com.ezra.productservice.enums.LoanStructure;
import com.ezra.productservice.enums.TenureType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductUpdateRequest {

    @Size(min = 1, max = 255, message = "Product name must be 1-255 characters")
    private String name;

    private String description;

    @DecimalMin(value = "0.01", message = "Minimum amount must be at least 0.01")
    private BigDecimal minAmount;

    @DecimalMin(value = "0.01", message = "Maximum amount must be at least 0.01")
    private BigDecimal maxAmount;

    @Min(value = 0, message = "Interest rate cannot be negative")
    private Integer interestRate;

    @DecimalMin(value = "1", message = "Tenure value must be at least 1")
    private BigDecimal tenureValue;

    private TenureType tenureType;
    private LoanStructure loanStructure;
    private Boolean active;
}
