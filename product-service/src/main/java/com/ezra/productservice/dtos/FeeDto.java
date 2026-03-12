package com.ezra.productservice.dtos;

import com.ezra.productservice.enums.FeeType;
import lombok.*;

import com.ezra.productservice.enums.CalcMethod;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeeDto {

    private UUID id;

    private String name;

    private FeeType feeType;

    private BigDecimal amount;

    private CalcMethod calculationMethod;

    private Integer daysAfterDue;

    private UUID productId;
}
