package com.app.ecommerce.payment.service;

import com.app.ecommerce.auth.security.AuthenticatedUserProvider;
import com.app.ecommerce.cart.entity.Cart;
import com.app.ecommerce.cart.entity.CartItem;
import com.app.ecommerce.cart.repository.CartRepository;
import com.app.ecommerce.cart.service.CartService;
import com.app.ecommerce.messaging.dto.OrderPaidEvent;
import com.app.ecommerce.order.entity.Order;
import com.app.ecommerce.order.entity.OrderItem;
import com.app.ecommerce.order.entity.OrderStatus;
import com.app.ecommerce.order.mapper.OrderMapper;
import com.app.ecommerce.order.service.OrderService;
import com.app.ecommerce.payment.dto.request.PaymentRequest;
import com.app.ecommerce.payment.dto.response.PaymentResponse;
import com.app.ecommerce.payment.dto.response.PaymentResult;
import com.app.ecommerce.payment.entity.Payment;
import com.app.ecommerce.payment.entity.PaymentProvider;
import com.app.ecommerce.payment.entity.PaymentStatus;
import com.app.ecommerce.payment.gateway.PaymentGateway;
import com.app.ecommerce.payment.mapper.PaymentMapper;
import com.app.ecommerce.payment.repository.PaymentRepository;
import com.app.ecommerce.product.repository.ProductRepository;
import com.app.ecommerce.shared.exceptions.BadRequestException;
import com.app.ecommerce.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final OrderService orderService;
    private final CartService cartService;
    private final PaymentGateway paymentGateway;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public PaymentResponse processPayment(PaymentRequest paymentRequest) {
        UUID userId = authenticatedUserProvider.getCurrentUserId();
        Cart cart = cartRepository.findActiveCartByUserIdWithItemsAndProducts(userId).orElseThrow(
                () -> new NotFoundException("Active cart not found for user.")
        );

        int cartUpdatedRows = cartRepository.convertCartStatusToConverted(cart.getId());
        if (cartUpdatedRows == 0) {
            throw new BadRequestException("Failed to convert cart status.");
        }

        List<CartItem> unselectedItems = cart.getUnselectedItems();
        Order order = orderService.create(cart);

        for (OrderItem item : order.getOrderItems()) {
            int updatedRows = productRepository.debitStock(item.getProduct().getId(), item.getQuantity());
            if (updatedRows == 0) {
                throw new BadRequestException("Insufficient stock for product: " + item.getProduct().getName());
            }
        }

        PaymentResult paymentResult = paymentGateway.charge(order.getTotalAmount(), UUID.randomUUID().toString());
        if (!paymentResult.success()) {
            throw new BadRequestException("Payment declined.");
        }
        order.setOrderStatus(OrderStatus.PAID);
        cartService.createCartAndAddUnselectedItems(unselectedItems);

        eventPublisher.publishEvent(new OrderPaidEvent(
                order.getId(),
                userId,
                OrderMapper.toSnapshot(order.getOrderItems()), order.getTotalAmount()));

        return PaymentMapper.toResponse(createPayment(
                order,
                paymentResult.transactionId().toString(),
                order.getTotalAmount(),
                paymentRequest.paymentProvider()));
    }

    private Payment createPayment(Order order, String transactionId, BigDecimal totalAmount, PaymentProvider paymentProvider) {
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentProvider(paymentProvider);
        payment.setPaymentStatus(PaymentStatus.APPROVED);
        payment.setTransactionId(transactionId);
        payment.setAmount(totalAmount);
        paymentRepository.save(payment);
        return payment;
    }
}
