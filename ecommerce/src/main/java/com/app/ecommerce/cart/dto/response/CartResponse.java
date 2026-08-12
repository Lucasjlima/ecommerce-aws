package com.app.ecommerce.cart.dto.response;

import com.app.ecommerce.cart.entity.CartStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record CartResponse(UUID cartId, CartStatus cartStatus, List<CartItemResponse> cartItemResponse,
                           BigDecimal total) {
}
