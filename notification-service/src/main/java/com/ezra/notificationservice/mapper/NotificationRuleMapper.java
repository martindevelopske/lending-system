package com.ezra.notificationservice.mapper;

import com.ezra.notificationservice.dto.RuleCreateRequest;
import com.ezra.notificationservice.dto.RuleResponse;
import com.ezra.notificationservice.models.NotificationRule;
import org.springframework.stereotype.Component;

@Component
public class NotificationRuleMapper {
    public NotificationRule toEntity(RuleCreateRequest request) {
        return NotificationRule.builder()
                .eventType(request.getEventType())
                .channel(request.getChannel())
                .priority(request.getPriority())
                .productId(request.getProductId())
                .customerSegment(request.getCustomerSegment())
                .active(true)
                .build();
    }

    public RuleResponse toResponse(NotificationRule rule) {
        return RuleResponse.builder()
                .id(rule.getId())
                .eventType(rule.getEventType())
                .channel(rule.getChannel())
                .priority(rule.getPriority())
                .productId(rule.getProductId())
                .customerSegment(rule.getCustomerSegment())
                .active(rule.getActive())
                .build();
    }
}
