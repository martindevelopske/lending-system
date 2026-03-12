package com.ezra.productservice.event;

import com.ezra.productservice.models.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductEventPublisher {
    private static final String PRODUCT_EVENT_TOPIC = "product-events";
    private final KafkaTemplate<String, ProductEvent> kafkaTemplate;

    public void publishProductCreatedEvent(Product product) {
        publish("PRODUCT_CREATED", product);
    }

    public void publishProductUpdatedEvent(Product product) {
        publish("PRODUCT_UPDATED", product);
    }

    private void publish(String eventType, Product product) {
        ProductEvent event = ProductEvent.builder()
                .eventType(eventType)
                .productId(product.getId())
                .productName(product.getName())
                .interestRate(product.getInterestRate())
                .loanStructure(product.getLoanStructure().name())
                .fees(product.getFees() != null ? product.getFees().stream().map(fee -> ProductEvent.FeeEvent.builder()
                        .feeId(fee.getId())
                        .name(fee.getName())
                        .feeType(fee.getFeeType().name())
                        .calculationMethod(fee.getCalculationMethod().name())
                        .amount(fee.getAmount())
                        .daysAfterDue(fee.getDaysAfterDue())
                        .build()).collect(Collectors.toList()) : null)
                .build();

        kafkaTemplate.send(PRODUCT_EVENT_TOPIC, product.getId().toString(), event);
        log.info("Published product event: {} for product: {}", eventType, product.getId());
    }
}
