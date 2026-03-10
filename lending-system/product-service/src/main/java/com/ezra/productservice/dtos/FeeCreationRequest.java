package com.ezra.productservice.dtos;
import com.ezra.productservice.enums.CalcMethod;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeeCreationRequest {
    private String name;

    private BigDecimal amount;

    private CalcMethod calculationMethod;

    private Integer daysAfterDue;

    private UUID productId;
}
