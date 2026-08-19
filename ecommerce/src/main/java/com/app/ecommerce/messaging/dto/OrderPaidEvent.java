package com.app.ecommerce.messaging.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderPaidEvent(UUID orderId, UUID userId, String userEmail, String userName,
                             List<OrderItemSnapshot> orderItems,
                             BigDecimal totalAmount) {
}
