package com.ezra.loanservice.models;

import com.ezra.loanservice.enums.LoanState;
import com.ezra.loanservice.enums.LoanStructure;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Core entity representing a loan in the system. Tracks the full lifecycle from
 * disbursement through repayment to closure or write-off.
 * Total amount = principal + service fee. Outstanding balance = total - amount paid.
 * Supports both LUMP_SUM (single payment) and INSTALLMENT (multiple payments) structures.
 */
@Entity
@Table(name = "loans")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "principal_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal principalAmount;

    @Column(name = "interest_rate", nullable = false, precision = 10, scale = 4)
    private BigDecimal interestRate;

    @Column(name = "service_fee", precision = 19, scale = 4)
    private BigDecimal serviceFee;

    @Column(name = "accrued_daily_fees", nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal accruedDailyFees = BigDecimal.ZERO;

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalAmount;

    @Column(name = "amount_paid", nullable = false, precision = 19, scale = 4)
    private BigDecimal amountPaid;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanState state;

    @Enumerated(EnumType.STRING)
    @Column(name = "loan_structure", nullable = false)
    private LoanStructure loanStructure;

    @Column(name = "tenure_value", nullable = false)
    private Integer tenureValue;

    @Column(name = "tenure_type", nullable = false)
    private String tenureType;

    @Column(name = "disbursement_date", nullable = false)
    private LocalDate disbursementDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Installment> installments = new HashSet<>();

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Repayment> repayments = new HashSet<>();

    @Column(name = "last_fee_accrual_date")
    private LocalDate lastFeeAccrualDate;

    @jakarta.persistence.PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
    }

    @jakarta.persistence.PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /** Returns the remaining amount owed (totalAmount - amountPaid). */
    public BigDecimal getOutstandingBalance() {
        return totalAmount.subtract(amountPaid);
    }

    /** Returns true if the loan has been fully repaid. */
    public boolean isFullyPaid() {
        return amountPaid.compareTo(totalAmount) >= 0;
    }

}
