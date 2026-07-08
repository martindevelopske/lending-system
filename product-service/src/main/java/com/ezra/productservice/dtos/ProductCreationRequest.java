package com.ezra.productservice.dtos;

import com.ezra.productservice.enums.LoanStructure;
import com.ezra.productservice.enums.TenureType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCreationRequest {

    @NotBlank(message = "Product name is required")
    private String name;

    private String description;

    @NotNull(message = "Tenure type is required")
    private TenureType tenureType;

    @NotNull(message = "Tenure value is required")
    @DecimalMin(value = "1", message = "Tenure value must be at least 1")
    private BigDecimal tenureValue;

    @NotNull(message = "Loan structure is required")
    private LoanStructure loanStructure;

    @NotNull(message = "Interest rate is required")
    @Min(value = 0, message = "Interest rate cannot be negative")
    private Integer interestRate;

    @NotNull(message = "Minimum amount is required")
    @DecimalMin(value = "0.01", message = "Minimum amount must be at least 0.01")
    private BigDecimal minimumAmount;

    @NotNull(message = "Maximum amount is required")
    @DecimalMin(value = "0.01", message = "Maximum amount must be at least 0.01")
    private BigDecimal maximumAmount;

    private Boolean isActive;

    private List<FeeDto> fees;
}
