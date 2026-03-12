package com.ezra.notificationservice.channel;

import com.ezra.notificationservice.enums.ChannelType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SmsChannel implements NotificationChannel{
    @Override
    public void send(String recipient, String subject, String content) {
        log.info("Sending SMS to {}", recipient);
        // todo integrate with sms service
    }

    @Override
    public ChannelType getChannelType() {
        return ChannelType.SMS;
    }
}
