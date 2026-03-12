package com.ezra.notificationservice.dto;

import com.ezra.notificationservice.enums.ChannelType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateCreateRequest {

    @NotBlank(message = "Event type is required")
    private String eventType;

    @NotNull
    private ChannelType channel;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Body template is required")
    private String bodyTemplate;

    private UUID productId;
}