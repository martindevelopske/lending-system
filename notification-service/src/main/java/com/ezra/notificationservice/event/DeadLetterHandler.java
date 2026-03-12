package com.ezra.notificationservice.event;

import com.ezra.notificationservice.dto.NotificationTask;
import com.ezra.notificationservice.enums.ChannelType;
import com.ezra.notificationservice.enums.NotificationStatus;
import com.ezra.notificationservice.models.NotificationLog;
import com.ezra.notificationservice.repository.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeadLetterHandler {
    private static final int MAX_RETRIES = 3;
    private final RabbitTemplate rabbitTemplate;
    private final NotificationLogRepository logRepository;

    @RabbitListener(queues = "notification.retry.queue")
    public void handleRetry(NotificationTask task) {
        log.info("Retrying notification for customer {}", task.getCustomerId());

        // Re-route to original queue
        String routingKey = "notification." + task.getChannel().toLowerCase();
        rabbitTemplate.convertAndSend("notification.exchange", routingKey, task);
    }

    @RabbitListener(queues = "notification.failed.queue")
    public void handleFailed(NotificationTask task) {
        log.error("Notification permanently failed for customer {}, event {}", task.getCustomerId(), task.getEventType());

        NotificationLog logEntry = NotificationLog.builder()
                .customerId(task.getCustomerId())
                .loanId(task.getLoanId())
                .channel(ChannelType.valueOf(task.getChannel()))
                .eventType(task.getEventType())
                .subject(task.getTemplateSubject())
                .content(task.getTemplateBody())
                .status(NotificationStatus.FAILED)
                .retryCount(MAX_RETRIES)
                .errorMessage("Max retries exceeded")
                .build();
        logRepository.save(logEntry);
    }
}
