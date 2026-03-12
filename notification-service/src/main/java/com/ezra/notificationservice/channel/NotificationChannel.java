package com.ezra.notificationservice.channel;

import com.ezra.notificationservice.enums.ChannelType;

public interface NotificationChannel {
    void send(String recipient, String subject, String content);
    ChannelType getChannelType();
}
