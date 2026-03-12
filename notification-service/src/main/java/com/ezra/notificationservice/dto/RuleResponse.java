package com.ezra.notificationservice.dto;

import com.ezra.notificationservice.enums.ChannelType;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuleResponse {
    private UUID id;
    private String eventType;
    private ChannelType channel;
    private Integer priority;
    private UUID productId;
    private String customerSegment;
    private Boolean active;
}