package com.app.ecommerce.messaging.listener;

import com.app.ecommerce.email.dto.PurchaseConfirmation;
import com.app.ecommerce.email.service.EmailService;
import com.app.ecommerce.messaging.dto.OrderPaidEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailNotificationListener {

    private final EmailService emailService;

    @RabbitListener(queues = "${app.rabbitmq.email.queue}")
    public void onEmailNotification(OrderPaidEvent orderPaidEvent) {
        emailService.sendPurchaseNotification(new PurchaseConfirmation(
                orderPaidEvent.orderId(),
                orderPaidEvent.userName(),
                orderPaidEvent.userEmail(),
                orderPaidEvent.totalAmount()));
    }
}
