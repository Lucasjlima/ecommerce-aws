package com.app.ecommerce.order.service;

import com.app.ecommerce.auth.security.AuthenticatedUserProvider;
import com.app.ecommerce.cart.entity.Cart;
import com.app.ecommerce.cart.entity.CartItem;
import com.app.ecommerce.cart.repository.CartRepository;
import com.app.ecommerce.order.dto.response.OrderResponse;
import com.app.ecommerce.order.entity.Order;
import com.app.ecommerce.order.entity.OrderItem;
import com.app.ecommerce.order.entity.OrderStatus;
import com.app.ecommerce.order.mapper.OrderMapper;
import com.app.ecommerce.order.repository.OrderRepository;
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
public class OrderService {

    private final OrderRepository orderRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final CartRepository cartRepository;

    @Transactional
    public OrderResponse create() {
        UUID userId = authenticatedUserProvider.getCurrentUserId();

        Cart cart = cartRepository.findActiveCartByUserIdWithItemsAndProducts(userId).orElseThrow(
                () -> new NotFoundException("Cart not found.")
        );

        // OrderService.create() — a validação mora no contexto de order
        List<CartItem> selectedItems = cart.getSelectedItems();
        if (selectedItems.isEmpty()) {
            throw new BadRequestException("Cannot create order. No selected items in the cart.");
        }

        Order order = new Order();
        order.setUser(cart.getUser());
        order.setOrderStatus(OrderStatus.PENDING);
        order.setCreatedAt(Instant.now());
        order.setTotalAmount(cart.calculateSelectedItemsTotal());
        order.setOrderItems(createOrderItems(order, selectedItems));
        orderRepository.save(order);
        return OrderMapper.toResponse(order);
    }

    private List<OrderItem> createOrderItems(Order order, List<CartItem> cartItems) {
        return cartItems.stream()
                .map(cartItem -> {
                            OrderItem orderItem = new OrderItem();
                            orderItem.setOrder(order);
                            orderItem.setProduct(cartItem.getProduct());
                            orderItem.setQuantity(cartItem.getQuantity());
                            orderItem.setPriceAtPurchase(cartItem.getProduct().getPrice());
                            return orderItem;
                        }
                ).toList();
    }
}

