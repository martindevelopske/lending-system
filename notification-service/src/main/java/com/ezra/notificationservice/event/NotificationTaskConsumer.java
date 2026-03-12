package com.ezra.notificationservice.event;

import com.ezra.notificationservice.dto.NotificationTask;
import com.ezra.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationTaskConsumer {

    private final NotificationService notificationService;

    @RabbitListener(queues = "notification.email.queue")
    public void handleEmailNotification(NotificationTask task) {
        log.info("Processing email notification: {}", task);
        notificationService.deliverNotification(task);
    }

    @RabbitListener(queues = "notification.sms.queue")
    public void handleSmsNotification(NotificationTask task) {
        log.info("Processing SMS notification for customer {}", task.getCustomerId());
        notificationService.deliverNotification(task);
    }

    @RabbitListener(queues = "notification.push.queue")
    public void handlePushNotification(NotificationTask task) {
        log.info("Processing push notification for customer {}", task.getCustomerId());
        notificationService.deliverNotification(task);
    }
}
