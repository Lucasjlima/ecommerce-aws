package com.app.ecommerce.cart.service;

import com.app.ecommerce.auth.security.AuthenticatedUserProvider;
import com.app.ecommerce.cart.dto.request.CartItemRequest;
import com.app.ecommerce.cart.dto.response.CartResponse;
import com.app.ecommerce.cart.entity.Cart;
import com.app.ecommerce.cart.entity.CartItem;
import com.app.ecommerce.cart.entity.CartStatus;
import com.app.ecommerce.cart.mapper.CartMapper;
import com.app.ecommerce.cart.repository.CartRepository;
import com.app.ecommerce.product.entity.Product;
import com.app.ecommerce.product.repository.ProductRepository;
import com.app.ecommerce.shared.exceptions.BadRequestException;
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
    public CartResponse addProductIntoCart(CartItemRequest cartItemRequest) {
        UUID userId = authenticatedUserProvider.getCurrentUserId();
        Product product = productRepository.findByIdAndActiveTrue(cartItemRequest.productId()).orElseThrow(
                () -> new NotFoundException("Product not found.")
        );
        Cart cart = cartRepository.findActiveCartByUserIdWithItemsAndProducts(userId).orElseGet(
                () -> createCart()
        );

        List<CartItem> cartItems = cart.getCartItems();
        if (cartItems != null) {
            for (CartItem items : cartItems) {
                if (items.getProduct().getId().equals(product.getId())) {
                    items.setQuantity(items.getQuantity() + cartItemRequest.quantity());
                    return CartMapper.toResponse(cart);
                }

            }
        }
        CartItem newCartItem = new CartItem();
        newCartItem.setCart(cart);
        newCartItem.setProduct(product);
        newCartItem.setQuantity(cartItemRequest.quantity());
        newCartItem.setSelected(true);
        cartItems.add(newCartItem);
        return CartMapper.toResponse(cart);
    }

    @Transactional
    public void createCartAndAddUnselectedItems(List<CartItem> unselectedItems) {
        if (unselectedItems.isEmpty()) return;
        Cart newCart = createCart();
        for (CartItem old : unselectedItems) {
            CartItem copy = new CartItem();
            copy.setCart(newCart);
            copy.setProduct(old.getProduct());
            copy.setQuantity(old.getQuantity());
            copy.setSelected(false);
            newCart.getCartItems().add(copy);
        }
    }

    @Transactional
    public CartResponse removeProductFromCart(UUID productId, Long quantity) {
        if (quantity == null || quantity <= 0) {
            throw new BadRequestException("Quantity must be a positive number");
        }
        UUID userId = authenticatedUserProvider.getCurrentUserId();
        Cart cart = cartRepository.findActiveCartByUserIdWithItemsAndProducts(userId).orElseThrow(
                () -> new NotFoundException("Active cart not found"));

        CartItem item = cart.getCartItems().stream()
                .filter(ci -> ci.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Product not in cart"));

        if (quantity > item.getQuantity()) {
            throw new BadRequestException("Quantity to remove exceeds quantity in cart");
        }

        if (quantity.equals(item.getQuantity())) {
            cart.getCartItems().remove(item);
        } else {
            item.setQuantity(item.getQuantity() - quantity);
        }
        return CartMapper.toResponse(cart);
    }

    @Transactional(readOnly = true)
    public CartResponse getCart() {
        UUID userId = authenticatedUserProvider.getCurrentUserId();
        Cart cart = cartRepository.findActiveCartByUserIdWithItemsAndProducts(userId).orElseThrow(
                () -> new NotFoundException("Active cart not found"));
        return CartMapper.toResponse(cart);
    }

    @Transactional
    public void toggleSelected(UUID productId) {
        UUID userId = authenticatedUserProvider.getCurrentUserId();
        Cart cart = cartRepository.findActiveCartByUserIdWithItemsAndProducts(userId).orElseThrow(
                () -> new NotFoundException("Active cart not found"));
        CartItem cartItem = cart.getCartItems().stream()
                .filter(ci -> ci.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Product not in cart"));
        cartItem.setSelected(!cartItem.getSelected());
    }


    private Cart createCart() {
        Cart cart = new Cart();
        cart.setUser(authenticatedUserProvider.getCurrentUser());
        cart.setCartStatus(CartStatus.ACTIVE);
        cart.setCreatedAt(Instant.now());
        cartRepository.save(cart);
        return cart;
    }


}

