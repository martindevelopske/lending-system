package com.ezra.notificationservice.channel;

import com.ezra.notificationservice.enums.ChannelType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EmailChannel implements NotificationChannel {
    @Override
    public void send(String recipient, String subject, String content) {
        log.info("Sending email to {}", recipient);
// todo integrate with email service
    }

    @Override
    public ChannelType getChannelType() {
        return ChannelType.EMAIL;
    }
}
