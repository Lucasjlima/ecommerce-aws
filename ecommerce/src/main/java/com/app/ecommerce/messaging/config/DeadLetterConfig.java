package com.app.ecommerce.messaging.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeadLetterConfig {

    @Value("${app.rabbitmq.dead-letter.exchange}")
    private String dlxExchange;

    @Value("${app.rabbitmq.order.dead-letter.routing-key}")
    private String dlqRoutingKey;

    @Value("${app.rabbitmq.order.dead-letter.queue}")
    private String dlqQueue;

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(dlxExchange, true, false);
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(dlqQueue).build();
    }

    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue)
                .to(deadLetterExchange)
                .with(dlqRoutingKey);
    }
}
