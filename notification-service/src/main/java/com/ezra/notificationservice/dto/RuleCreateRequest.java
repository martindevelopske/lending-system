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
public class RuleCreateRequest {
    @NotBlank(message = "Event type is required")
    private String eventType;

    @NotNull
    private ChannelType channel;

    @NotNull
    private Integer priority;

    private UUID productId;
    private String customerSegment;
}
