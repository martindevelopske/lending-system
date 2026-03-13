package com.ezra.loanservice.event;

import com.ezra.loanservice.models.Loan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Publishes loan lifecycle events to the Kafka "loan.events" topic.
 * Events are keyed by customerId to ensure all events for a customer
 * are routed to the same partition for ordered processing.
 * Consumed by the notification-service for multi-channel notifications.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LoanEventPublisher {

    private static final String LOAN_EVENT_TOPIC = "loan.events";
    private final KafkaTemplate<String, LoanEvent> kafkaTemplate;

    public void publishLoanEvent(String eventType, Loan loan) {
        LoanEvent event = LoanEvent.builder()
                .eventType(eventType)
                .loanId(loan.getId())
                .customerId(loan.getCustomerId())
                .productId(loan.getProductId())
                .productName(loan.getProductName())
                .amount(loan.getPrincipalAmount())
                .outstandingBalance(loan.getOutstandingBalance())
                .loanState(loan.getState().name())
                .build();

        kafkaTemplate.send(LOAN_EVENT_TOPIC, loan.getCustomerId().toString(), event);
        log.info("Published loan event: {} for loan: {}", eventType, loan.getId());
    }
}
