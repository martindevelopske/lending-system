package com.ezra.customerservice.event;

import com.ezra.customerservice.models.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerEventPublisher {

    private static final String CUSTOMER_EVENT_TOPIC = "customer.events";
    private final KafkaTemplate<String, Map<String, Object>> kafkaTemplate;

    public void publishCustomerCreatedEvent(Customer customer) {
        publish("CUSTOMER_CREATED", customer);
    }

    public void publishCustomerUpdatedEvent(Customer customer) {
        publish("CUSTOMER_UPDATED", customer);
    }

    private void publish(String eventType, Customer customer) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", eventType);
        event.put("customerId", customer.getId().toString());
        event.put("firstName", customer.getFirstName());
        event.put("lastName", customer.getLastName());
        event.put("email", customer.getEmail());
        event.put("phoneNumber", customer.getPhoneNumber());
        event.put("status", customer.getStatus().name());

        kafkaTemplate.send(CUSTOMER_EVENT_TOPIC, customer.getId().toString(), event);

        log.info("Published customer event: {} for customer: {}", eventType, customer.getId());

    }

}
