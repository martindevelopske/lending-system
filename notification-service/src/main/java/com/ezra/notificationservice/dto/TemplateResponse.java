package com.ezra.notificationservice.dto;

import com.ezra.notificationservice.enums.ChannelType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateResponse {
    private UUID id;
    private String eventType;
    private ChannelType channel;
    private String subject;
    private String bodyTemplate;
    private UUID productId;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
