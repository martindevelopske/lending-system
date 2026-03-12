package com.ezra.notificationservice.service;

import com.ezra.notificationservice.dto.NotificationLogResponse;
import com.ezra.notificationservice.dto.NotificationTask;
import com.ezra.notificationservice.dto.RuleCreateRequest;
import com.ezra.notificationservice.dto.RuleResponse;
import com.ezra.notificationservice.mapper.NotificationLogMapper;
import com.ezra.notificationservice.mapper.NotificationRuleMapper;
import com.ezra.notificationservice.models.NotificationRule;
import com.ezra.notificationservice.models.NotificationTemplate;
import com.ezra.notificationservice.repository.NotificationLogRepository;
import com.ezra.notificationservice.repository.NotificationRuleRepository;
import com.ezra.notificationservice.repository.NotificationTemplateRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRuleRepository notificationRuleRepository;
    private final NotificationTemplateRepository notificationTemplateRepository;
    private final NotificationLogRepository notificationLogRepository;
    private final NotificationRuleMapper notificationRuleMapper;
    private final NotificationLogMapper notificationLogMapper;

    @Override
    public void processLoanEvent(String eventType, UUID customerId, UUID loanId, UUID productId, Map<String, String> variables) {
        log.info("Processing loan event: {}", eventType);
        List<NotificationRule> rules = notificationRuleRepository.findActiveRulesForEvent(eventType);

        for (NotificationRule rule : rules) {
            List<NotificationTemplate> templates = notificationTemplateRepository.findTemplatesForEvent(eventType, rule.getChannel(), productId);

            if (templates.isEmpty()) {
                log.warn("No templates found for event: {} and channel: {}", eventType, rule.getChannel());
                continue;
            }

            NotificationTemplate template = templates.get(0); //firt template is used
            NotificationTask task = NotificationTask.builder()
                    .customerId(customerId)
                    .loanId(loanId)
                    .eventType(eventType)
                    .channel(rule.getChannel().name())
                    .templateSubject(template.getSubject())
                    .templateBody(template.getBodyTemplate())
                    .variables(variables)
                    .productId(productId)
                    .build();

            String routingKey = "notification" + rule.getChannel().name().toLowerCase();
            // todo send with rabbit mq
//            rabbitTemplate.convertAndSend("notification.exchange", routingKey, task);

            log.info("Published notification task: {} for customer: {}", task, customerId);
        }
    }

    @Transactional
    @Override
    public RuleResponse createRule(RuleCreateRequest request) {
        NotificationRule rule = notificationRuleMapper.toEntity(request);
        rule = notificationRuleRepository.save(rule);
        return notificationRuleMapper.toResponse(rule);
    }

    @Transactional
    @Override
    public List<RuleResponse> getAllRules() {
        return notificationRuleRepository.findAll().stream()
                .map(notificationRuleMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<NotificationLogResponse> getCustomerNotifications(UUID customerId) {
        return notificationLogRepository.findByCustomerId(customerId).stream()
                .map(notificationLogMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<NotificationLogResponse> getLoanNotifications(UUID loanId) {
        return notificationLogRepository.findByLoanId(loanId).stream()
                .map(notificationLogMapper::toResponse)
                .collect(Collectors.toList());
    }
}
