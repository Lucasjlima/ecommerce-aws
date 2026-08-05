package com.app.ecommerce.cart.service;

import com.app.ecommerce.auth.security.AuthenticatedUserProvider;
import com.app.ecommerce.cart.dto.request.CartItemRequest;
import com.app.ecommerce.cart.entity.Cart;
import com.app.ecommerce.cart.entity.CartItem;
import com.app.ecommerce.cart.entity.CartStatus;
import com.app.ecommerce.cart.repository.CartRepository;
import com.app.ecommerce.product.entity.Product;
import com.app.ecommerce.product.repository.ProductRepository;
import com.app.ecommerce.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Transactional
    public Cart addProductIntoCart(CartItemRequest cartItemRequest) {
        UUID userId = authenticatedUserProvider.getCurrentUserId();
        Product product = productRepository.findById(cartItemRequest.productId()).orElseThrow(
                () -> new NotFoundException("Product not found.")
        );
        Cart cart = cartRepository.findByUserIdAndCartStatusActive(userId).orElseGet(
                () -> createCart()
        );

        List<CartItem> cartItems = cart.getCartItems();
        if (cartItems != null) {
            for (CartItem items : cartItems) {
                if (items.getProduct().getId().equals(product.getId())) {
                    items.setQuantity(items.getQuantity() + cartItemRequest.quantity());
                    return cart;
                }

            }
        }
        CartItem newCartItem = new CartItem();
        newCartItem.setCart(cart);
        newCartItem.setProduct(product);
        newCartItem.setQuantity(cartItemRequest.quantity());
        cartItems.add(newCartItem);
        return cart;
    }


    public Cart createCart() {
        Cart cart = new Cart();
        cart.setUser(authenticatedUserProvider.getCurrentUser());
        cart.setCartStatus(CartStatus.ACTIVE);
        cart.setCreatedAt(Instant.now());
        cartRepository.save(cart);
        return cart;
    }
}

