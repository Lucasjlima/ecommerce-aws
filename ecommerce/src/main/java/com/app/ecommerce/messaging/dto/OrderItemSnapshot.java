package com.app.ecommerce.messaging.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record OrderItemSnapshot(UUID productId, Long quantity, BigDecimal price
) {
}