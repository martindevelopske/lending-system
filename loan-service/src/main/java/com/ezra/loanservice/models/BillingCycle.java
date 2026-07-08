package com.ezra.loanservice.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a customer's preferred billing cycle. When set, all INSTALLMENT loans
 * for this customer have their installment due dates aligned to the specified day
 * of the month. This consolidates multiple loans under a single billing date,
 * simplifying repayment for customers with multiple active loans.
 *
 * The billing day is restricted to 1-28 to avoid month-end edge cases
 * (not all months have days 29, 30, or 31).
 */
@Entity
@Table(name = "billing_cycles")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingCycle {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "customer_id", nullable = false, unique = true)
    private UUID customerId;

    @Column(name = "billing_day_of_month", nullable = false)
    private Integer billingDayOfMonth;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
