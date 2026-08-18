package com.app.ecommerce.payment.mapper;

import com.app.ecommerce.payment.dto.response.PaymentResponse;
import com.app.ecommerce.payment.entity.Payment;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PaymentMapper {
    public static PaymentResponse toResponse(Payment payment) {
        return PaymentResponse
                .builder()
                .transactionId(payment.getTransactionId())
                .paymentStatus(payment.getPaymentStatus())
                .amount(payment.getAmount())
                .paymentProvider(payment.getPaymentProvider())
                .build();
    }
}
