package com.app.ecommerce.order.mapper;

import com.app.ecommerce.messaging.dto.OrderItemSnapshot;
import com.app.ecommerce.order.dto.response.OrderItemResponse;
import com.app.ecommerce.order.dto.response.OrderResponse;
import com.app.ecommerce.order.entity.Order;
import com.app.ecommerce.order.entity.OrderItem;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class OrderMapper {
    public static OrderResponse toResponse(Order order) {
        return OrderResponse
                .builder()
                .orderId(order.getId())
                .orderStatus(order.getOrderStatus())
                .totalAmount(order.getTotalAmount())
                .orderItems(toOrderItemResponse(order.getOrderItems()))
                .build();
    }

    public static List<OrderItemSnapshot> toSnapshot(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(orderItem ->
                    OrderItemSnapshot
                            .builder()
                            .productId(orderItem.getProduct().getId())
                            .quantity(orderItem.getQuantity())
                            .price(orderItem.getPriceAtPurchase())
                            .build()).toList();
    }

    private static List<OrderItemResponse> toOrderItemResponse(List<OrderItem> orderItem) {
        return orderItem.stream()
                .map(orderItems ->
                        OrderItemResponse
                                .builder()
                                .id(orderItems.getId())
                                .productName(orderItems.getProduct().getName())
                                .quantity(orderItems.getQuantity())
                                .priceAtPurchase(orderItems.getPriceAtPurchase())
                                .build()).toList();
    }
}
