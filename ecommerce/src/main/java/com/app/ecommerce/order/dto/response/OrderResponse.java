package com.app.ecommerce.order.dto.response;

import com.app.ecommerce.order.entity.OrderStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record OrderResponse(UUID orderId, OrderStatus orderStatus, BigDecimal totalAmount,
                            List<OrderItemResponse> orderItems) {
}
