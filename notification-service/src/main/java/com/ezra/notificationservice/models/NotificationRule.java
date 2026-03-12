package com.ezra.notificationservice.models;

import com.ezra.notificationservice.enums.ChannelType;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "notification_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRule {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChannelType channel;

    @Column(nullable = false)
    private Integer priority;

    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "customer_segment")
    private String customerSegment;

    @Column(nullable = false)
    private Boolean active = true;
}
