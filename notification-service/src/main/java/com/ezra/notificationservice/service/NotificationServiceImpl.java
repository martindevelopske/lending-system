package com.ezra.notificationservice.service;

import com.ezra.notificationservice.dto.NotificationTask;
import com.ezra.notificationservice.dto.RuleCreateRequest;
import com.ezra.notificationservice.dto.RuleResponse;
import com.ezra.notificationservice.mapper.NotificationRuleMapper;
import com.ezra.notificationservice.models.NotificationRule;
import com.ezra.notificationservice.models.NotificationTemplate;
import com.ezra.notificationservice.repository.NotificationRuleRepository;
import com.ezra.notificationservice.repository.NotificationTemplateRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRuleRepository notificationRuleRepository;
    private final NotificationTemplateRepository notificationTemplateRepository;
    private final NotificationRuleMapper notificationRuleMapper;

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
    public RuleResponse createRule(RuleCreateRequest request) {
        NotificationRule rule = notificationRuleMapper.toEntity(request);
        rule = notificationRuleRepository.save(rule);
        return notificationRuleMapper.toResponse(rule);
    }

    @Transactional
    public List<RuleResponse> getAllRules() {
        return notificationRuleRepository.findAll().stream()
                .map(notificationRuleMapper::toResponse)
                .toList();
    }


}
