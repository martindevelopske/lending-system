package com.ezra.notificationservice.mapper;


import com.ezra.notificationservice.dto.NotificationLogResponse;
import com.ezra.notificationservice.models.NotificationLog;
import org.springframework.stereotype.Component;

@Component
public class NotificationLogMapper {

    public NotificationLogResponse toResponse(NotificationLog log) {
        return NotificationLogResponse.builder()
                .id(log.getId())
                .customerId(log.getCustomerId())
                .loanId(log.getLoanId())
                .channel(log.getChannel())
                .eventType(log.getEventType())
                .subject(log.getSubject())
                .content(log.getContent())
                .status(log.getStatus())
                .retryCount(log.getRetryCount())
                .errorMessage(log.getErrorMessage())
                .sentAt(log.getSentAt())
                .build();
    }
}