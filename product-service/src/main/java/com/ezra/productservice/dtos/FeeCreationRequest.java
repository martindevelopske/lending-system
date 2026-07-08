package com.ezra.productservice.dtos;
import com.ezra.productservice.enums.CalcMethod;
import com.ezra.productservice.enums.FeeType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeeCreationRequest {

    @NotBlank(message = "Fee name is required")
    private String name;

    @NotNull(message = "Fee amount is required")
    @DecimalMin(value = "0.01", message = "Fee amount must be at least 0.01")
    private BigDecimal amount;

    @NotNull(message = "Fee type is required")
    private FeeType feeType;

    @NotNull(message = "Calculation method is required")
    private CalcMethod calculationMethod;

    @Min(value = 0, message = "Days after due cannot be negative")
    private Integer daysAfterDue;

    private UUID productId;
}
