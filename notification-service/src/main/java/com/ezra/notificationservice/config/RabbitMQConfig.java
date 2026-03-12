package com.ezra.notificationservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    //main exchange
    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange("notification.exchange");
    }

    //dead letter exchange
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange("notification.dlx");
    }
    //queues with dlx

    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable("notification.email.queue").withArgument("x-dead-letter-exchange", "notification.dlx")
                .withArgument("x-dead-letter-routing-key", "notification.retry").build();
    }

    @Bean
    public Queue smsQueue() {
        return QueueBuilder.durable("notification.email.queue").withArgument("x-dead-letter-exchange", "notification.dlx")
                .withArgument("x-dead-letter-routing-key", "notification.retry").build();
    }

    @Bean
    public Queue pushQueue() {
        return QueueBuilder.durable("notification.email.queue").withArgument("x-dead-letter-exchange", "notification.dlx")
                .withArgument("x-dead-letter-routing-key", "notification.retry").build();
    }

    @Bean
    public Queue retryQueue() {
        return QueueBuilder.durable("notification.retry.queue")
                .withArgument("x-dead-letter-exchange", "notification.dlx")
                .withArgument("x-dead-letter-routing-key", "notification.failed")
                .withArgument("x-message-ttl", 300000) // five mins
                .build();
    }

    // Failed queue (permanent failures)
    @Bean
    public Queue failedQueue() {
        return QueueBuilder.durable("notification.failed.queue").build();
    }

    // Bindings - main exchange to channel queues
    @Bean
    public Binding emailBinding() {
        return BindingBuilder.bind(emailQueue()).to(notificationExchange()).with("notification.email");
    }

    @Bean
    public Binding smsBinding() {
        return BindingBuilder.bind(smsQueue()).to(notificationExchange()).with("notification.sms");
    }

    @Bean
    public Binding pushBinding() {
        return BindingBuilder.bind(pushQueue()).to(notificationExchange()).with("notification.push");
    }

    // Bindings - DLX to retry and failed queues
    @Bean
    public Binding retryBinding() {
        return BindingBuilder.bind(retryQueue()).to(deadLetterExchange()).with("notification.retry");
    }

    @Bean
    public Binding failedBinding() {
        return BindingBuilder.bind(failedQueue()).to(deadLetterExchange()).with("notification.failed");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
