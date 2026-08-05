package com.app.ecommerce.cart.controller;

import com.app.ecommerce.cart.dto.request.CartItemRequest;
import com.app.ecommerce.cart.dto.response.CartResponse;
import com.app.ecommerce.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping
    public ResponseEntity<CartResponse> addProductIntoCart(@RequestBody @Valid CartItemRequest cartItemRequest) {
        CartResponse cartResponse = cartService.addProductIntoCart(cartItemRequest);
        return ResponseEntity
                .created(URI.create("/api/v1/cart/" + cartResponse.cartId()))
                .body(cartResponse);
    }

}
