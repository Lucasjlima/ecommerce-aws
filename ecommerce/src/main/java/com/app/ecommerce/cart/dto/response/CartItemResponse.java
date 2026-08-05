package com.app.ecommerce.cart.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CartItemResponse(UUID productId, Long quantity) {
}
