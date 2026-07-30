package com.app.ecommerce.messaging.publisher;

import com.app.ecommerce.messaging.dto.OrderItemSnapshot;
import com.app.ecommerce.messaging.dto.OrderPaidEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderEventPublisher {

    @Value("${app.rabbitmq.order.exchange}")
    private String orderExchange;

    @Value("${app.rabbitmq.order.paid}")
    private String orderPaidRoutingKey;

    private final RabbitTemplate rabbitTemplate;

    public void publish(UUID orderId, UUID userId, List<OrderItemSnapshot> orderItems, BigDecimal totalAmount) {
        OrderPaidEvent event = new OrderPaidEvent(orderId, userId, orderItems, totalAmount);
        rabbitTemplate.convertAndSend(
                orderExchange,
                orderPaidRoutingKey,
                event
        );
    }
}
