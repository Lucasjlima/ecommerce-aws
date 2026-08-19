package com.app.ecommerce.messaging.listener;

import com.app.ecommerce.messaging.dto.OrderPaidEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class InvoiceGenerationListener {

    @RabbitListener(queues = "${app.rabbitmq.invoice.queue}")
    public void onInvoiceGeneration(OrderPaidEvent orderPaidEvent) {
        log.info("Invoice generation requested for order {}", orderPaidEvent.orderId());
    }
}
