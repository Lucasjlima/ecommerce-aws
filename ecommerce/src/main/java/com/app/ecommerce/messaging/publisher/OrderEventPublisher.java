package com.app.ecommerce.messaging.publisher;

import com.app.ecommerce.messaging.dto.OrderPaidEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventPublisher {

    @Value("${app.rabbitmq.order.exchange}")
    private String orderExchange;

    @Value("${app.rabbitmq.order.paid}")
    private String orderPaidRoutingKey;

    private final RabbitTemplate rabbitTemplate;

    public void publish(OrderPaidEvent event) {
        rabbitTemplate.convertAndSend(
                orderExchange,
                orderPaidRoutingKey,
                event
        );
    }
}
