package com.ezra.notificationservice.mapper;

import com.ezra.notificationservice.dto.TemplateCreateRequest;
import com.ezra.notificationservice.dto.TemplateResponse;
import com.ezra.notificationservice.models.NotificationTemplate;
import org.springframework.stereotype.Component;

@Component
public class TemplateMapper {
    public NotificationTemplate toEntity(TemplateCreateRequest request) {
        return NotificationTemplate.builder()
                .eventType(request.getEventType())
                .channel(request.getChannel())
                .subject(request.getSubject())
                .bodyTemplate(request.getBodyTemplate())
                .productId(request.getProductId())
                .isActive(true)
                .build();
    }

    public TemplateResponse toResponse(NotificationTemplate template) {
        return TemplateResponse.builder()
                .id(template.getId())
                .eventType(template.getEventType())
                .channel(template.getChannel())
                .subject(template.getSubject())
                .bodyTemplate(template.getBodyTemplate())
                .productId(template.getProductId())
                .active(template.getIsActive())
                .createdAt(template.getCreatedAt())
                .updatedAt(template.getUpdatedAt())
                .build();
    }
}
