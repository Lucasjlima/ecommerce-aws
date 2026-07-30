package com.app.ecommerce.messaging.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemSnapshot(UUID productId, Long quantity, BigDecimal price
){}