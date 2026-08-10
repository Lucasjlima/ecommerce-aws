package com.app.ecommerce.product.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductUpdateRequest(String name, String description, BigDecimal price, Long stockQuantity,
                                   UUID categoryId) {
}
