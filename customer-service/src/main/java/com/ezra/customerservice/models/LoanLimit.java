package com.ezra.customerservice.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "loan_limits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanLimit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "max_loan_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal maxLoanAmount;

    @Column(name = "available_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal availableAmount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false, unique = true)
    private Customer customer;
}
