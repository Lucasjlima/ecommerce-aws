package com.app.ecommerce.messaging.listener;

import com.app.ecommerce.messaging.dto.OrderPaidEvent;
import com.app.ecommerce.messaging.publisher.OrderEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final OrderEventPublisher publisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderPaid(OrderPaidEvent orderPaidEvent) {
        publisher.publish(orderPaidEvent);
    }
}
