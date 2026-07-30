package com.app.ecommerce.product.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record ProductResponse(UUID id, String name, String description, BigDecimal price, Long stockQuantity, UUID categoryId,
                              String imgKey) {
}
