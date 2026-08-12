package com.app.ecommerce.product.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductUpdateResponse(String name, String description, BigDecimal price, Long stockQuantity,
                                    CategoryResponse categoryResponse, String imgKey) {
}
