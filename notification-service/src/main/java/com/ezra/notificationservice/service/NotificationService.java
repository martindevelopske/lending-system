package com.ezra.notificationservice.service;

import com.ezra.notificationservice.dto.NotificationLogResponse;
import com.ezra.notificationservice.dto.NotificationTask;
import com.ezra.notificationservice.dto.RuleCreateRequest;
import com.ezra.notificationservice.dto.RuleResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface NotificationService {
    void processLoanEvent(String eventType, UUID customerId, UUID loanId, UUID productId, Map<String, String> variables);

    @Transactional
    RuleResponse createRule(RuleCreateRequest request);

    @Transactional
    List<RuleResponse> getAllRules();

    @Transactional(readOnly = true)
    List<NotificationLogResponse> getCustomerNotifications(UUID customerId);

    @Transactional(readOnly = true)
    List<NotificationLogResponse> getLoanNotifications(UUID loanId);

    void deliverNotification(NotificationTask task);
}
