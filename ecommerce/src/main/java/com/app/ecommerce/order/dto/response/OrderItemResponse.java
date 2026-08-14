package com.app.ecommerce.order.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record OrderItemResponse(UUID id, String productName, Long quantity, BigDecimal priceAtPurchase) {
}
