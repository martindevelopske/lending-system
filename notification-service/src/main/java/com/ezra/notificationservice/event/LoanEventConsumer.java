package com.ezra.notificationservice.event;

import com.ezra.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoanEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "loan.events", groupId = "notification-service-group")
    public void handleLoanEvent(Map<String, Object> eventData) {
        log.info("Received loan event: {}", eventData);
        String eventType = (String) eventData.get("eventType");
        String customerId = (String) eventData.get("customerId");
        String loanId = (String) eventData.get("loanId");
        String productId = (String) eventData.get("productId");

        Map<String, String> variables = new HashMap<>();
        variables.put("loanId", loanId != null ? loanId : "");
        variables.put("customerId", customerId != null ? customerId : "");
        variables.put("productName", eventData.get("productName").toString());
        variables.put("amount", eventData.get("amount").toString());
        variables.put("outstandingBalance", eventData.get("outstandingBalance").toString());
        variables.put("loanState", eventData.get("loanState") != null ? eventData.get("loanState").toString() : "");

        //add customer contact info if available
        if (eventData.containsKey("customerEmail"))
            variables.put("customerEmail", eventData.get("customerEmail").toString());
        if (eventData.get("customerPhone") != null)
            variables.put("phoneNumber", eventData.get("customerPhone").toString());
        if (eventData.get("customerFirstName") != null)
            variables.put("firstName", eventData.get("customerFirstName").toString());
        if (eventData.get("customerLastName") != null)
            variables.put("lastName", eventData.get("customerLastName").toString());

        try{
            notificationService.processLoanEvent(
                    eventType, customerId != null ? UUID.fromString(customerId) : null,
                    loanId != null ? UUID.fromString(loanId) : null,
                    productId != null ? UUID.fromString(productId) : null,
                    variables
            );
        } catch (Exception e) {
            log.error("Error processing loan event: {}", e.getMessage());
        }
    }
}
