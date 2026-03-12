package com.ezra.notificationservice.dto;

import lombok.*;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationTask implements Serializable {

    private UUID customerId;
    private UUID loanId;
    private String eventType;
    private String channel;
    private String templateSubject;
    private String templateBody;
    private Map<String, String> variables;
    private UUID productId;
}
