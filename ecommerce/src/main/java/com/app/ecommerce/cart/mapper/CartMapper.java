package com.app.ecommerce.cart.mapper;

import com.app.ecommerce.cart.dto.response.CartItemResponse;
import com.app.ecommerce.cart.dto.response.CartResponse;
import com.app.ecommerce.cart.entity.Cart;
import com.app.ecommerce.cart.entity.CartItem;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.List;

@UtilityClass
public class CartMapper {
    public static CartResponse toResponse(Cart cart) {
        List<CartItemResponse> cartItemResponse = toCartItemResponse(cart.getCartItems());
        return CartResponse
                .builder()
                .cartId(cart.getId())
                .cartStatus(cart.getCartStatus())
                .cartItemResponse(cartItemResponse)
                .total(calculateTotal(cartItemResponse))
                .build();

    }

    private static List<CartItemResponse> toCartItemResponse(List<CartItem> cartItems) {
        return cartItems.stream()
                .map(cartItem -> {
                    BigDecimal unitPrice = cartItem.getProduct().getPrice();
                    return CartItemResponse.builder()
                            .productId(cartItem.getProduct().getId())
                            .quantity(cartItem.getQuantity())
                            .selected(cartItem.getSelected())
                            .unitPrice(unitPrice)
                            .subtotal(unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                            .build();
                })
                .toList();
    }

    private static BigDecimal calculateTotal(List<CartItemResponse> cartItemResponse) {
        return cartItemResponse.stream()
                .filter(ci -> Boolean.TRUE.equals(ci.selected()))
                .map(CartItemResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
