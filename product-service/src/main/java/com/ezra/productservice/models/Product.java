package com.ezra.productservice.models;

import com.ezra.productservice.enums.LoanStructure;
import com.ezra.productservice.enums.TenureType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "product")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @Column(nullable = false, name = "tenure_type")
    @Enumerated(EnumType.STRING)
    private TenureType tenureType;

    @Column(nullable = false, name = "tenure_value")
    private BigDecimal tenureValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "loan_structure", nullable = false)
    private LoanStructure loanStructure;

    @Column(nullable = false, name = "interest_rate", precision = 5, scale = 2)
    private Integer interestRate;

    @Column(nullable = false, name = "min_amount", precision = 19, scale = 4)
    private BigDecimal minimumAmount;

    @Column(nullable = false, name = "max_amount", precision = 19, scale = 4)
    private BigDecimal maximumAmount;

    @Column(nullable = false, name = "is_active")
    private Boolean isActive = true;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Fee> fees;

    public void addFee(Fee fee) {
        fees.add(fee);
        fee.setProduct(this);
    }

    public void removeFee(Fee fee) {
        fees.remove(fee);
        fee.setProduct(null);
    }
}
