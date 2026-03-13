package com.ezra.notificationservice.service;

import com.ezra.notificationservice.channel.NotificationChannel;
import com.ezra.notificationservice.dto.NotificationLogResponse;
import com.ezra.notificationservice.dto.NotificationTask;
import com.ezra.notificationservice.dto.RuleCreateRequest;
import com.ezra.notificationservice.dto.RuleResponse;
import com.ezra.notificationservice.enums.ChannelType;
import com.ezra.notificationservice.enums.NotificationStatus;
import com.ezra.notificationservice.mapper.NotificationLogMapper;
import com.ezra.notificationservice.mapper.NotificationRuleMapper;
import com.ezra.notificationservice.models.NotificationLog;
import com.ezra.notificationservice.models.NotificationRule;
import com.ezra.notificationservice.models.NotificationTemplate;
import com.ezra.notificationservice.repository.NotificationLogRepository;
import com.ezra.notificationservice.repository.NotificationRuleRepository;
import com.ezra.notificationservice.repository.NotificationTemplateRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Core notification service that processes loan events into multi-channel notifications.
 * When a loan event arrives, it looks up active notification rules to determine which
 * channels (EMAIL, SMS, PUSH) should receive notifications, finds matching templates,
 * and publishes notification tasks to RabbitMQ for asynchronous delivery.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRuleRepository notificationRuleRepository;
    private final NotificationTemplateRepository notificationTemplateRepository;
    private final NotificationLogRepository notificationLogRepository;
    private final NotificationRuleMapper notificationRuleMapper;
    private final NotificationLogMapper notificationLogMapper;
    private final RabbitTemplate rabbitTemplate;
    private final TemplateRenderer templateRenderer;
    private final List<NotificationChannel> channels;

    /**
     * Processes an incoming loan event by looking up active rules, matching templates,
     * and publishing notification tasks to RabbitMQ channel-specific queues.
     * Product-specific templates take priority over global templates.
     */
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

            // Use the first matching template (product-specific templates are sorted first)
            NotificationTemplate template = templates.get(0);
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

            // Route to channel-specific queue (e.g., "notificationemail" → notification.email.queue)
            String routingKey = "notification" + rule.getChannel().name().toLowerCase();
            rabbitTemplate.convertAndSend("notification.exchange", routingKey, task);

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

    /**
     * Delivers a notification by rendering the template with variables, selecting the
     * appropriate channel implementation, and sending. Logs the result (SENT or FAILED)
     * to the notification_logs table. Failed deliveries are re-thrown for DLQ handling.
     */
    @Override
    public void deliverNotification(NotificationTask task) {
        String renderedSubject = templateRenderer.render(task.getTemplateSubject(), task.getVariables());
        String renderedContent = templateRenderer.render(task.getTemplateBody(), task.getVariables());

        ChannelType channelType = ChannelType.valueOf(task.getChannel());
        NotificationChannel channel = channels.stream()
                .filter(c -> c.getChannelType() == channelType)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No channel implementation for: " + task.getChannel()));

        String recipient = task.getVariables().getOrDefault("email",
                task.getVariables().getOrDefault("phoneNumber", "unknown"));

        try {
            channel.send(recipient, renderedSubject, renderedContent);

            NotificationLog logEntry = NotificationLog.builder()
                    .customerId(task.getCustomerId())
                    .loanId(task.getLoanId())
                    .channel(channelType)
                    .eventType(task.getEventType())
                    .subject(renderedSubject)
                    .content(renderedContent)
                    .status(NotificationStatus.SENT)
                    .retryCount(0)
                    .build();
            notificationLogRepository.save(logEntry);

        } catch (Exception e) {
            log.error("Failed to deliver notification: {}", e.getMessage());

            NotificationLog logEntry = NotificationLog.builder()
                    .customerId(task.getCustomerId())
                    .loanId(task.getLoanId())
                    .channel(channelType)
                    .eventType(task.getEventType())
                    .subject(renderedSubject)
                    .content(renderedContent)
                    .status(NotificationStatus.FAILED)
                    .retryCount(0)
                    .errorMessage(e.getMessage())
                    .build();
            notificationLogRepository.save(logEntry);
            throw e;
        }
    }
}
