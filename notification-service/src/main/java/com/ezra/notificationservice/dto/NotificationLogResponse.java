package com.ezra.notificationservice.dto;

import com.ezra.notificationservice.enums.ChannelType;
import com.ezra.notificationservice.enums.NotificationStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationLogResponse {
    private UUID id;
    private UUID customerId;
    private UUID loanId;
    private ChannelType channel;
    private String eventType;
    private String subject;
    private String content;
    private NotificationStatus status;
    private Integer retryCount;
    private String errorMessage;
    private LocalDateTime sentAt;
}
