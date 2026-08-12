package com.app.ecommerce.cart.controller;

import com.app.ecommerce.cart.dto.request.CartItemRequest;
import com.app.ecommerce.cart.dto.response.CartResponse;
import com.app.ecommerce.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

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

    @PutMapping("/{productId}")
    public ResponseEntity<CartResponse> removeProductFromCart(@PathVariable UUID productId, @RequestParam Long quantity) {
        return ResponseEntity.ok(cartService.removeProductFromCart(productId, quantity));
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart() {
        return ResponseEntity.ok(cartService.getCart());
    }

}
