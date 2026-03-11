package com.ezra.productservice.event;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductEvent {
    private String eventType;
    private UUID productId;
    private String productName;
    private Integer interestRate;
    private String loanStructure;
    private List<FeeEvent> fees;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FeeEvent {
        private UUID feeId;
        private String name;
        private String feeType;
        private BigDecimal amount;
        private String calculationMethod;
        private Integer daysAfterDue;
    }
}


