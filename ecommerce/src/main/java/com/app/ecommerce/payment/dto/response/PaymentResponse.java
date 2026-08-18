package com.app.ecommerce.payment.dto.response;

import com.app.ecommerce.payment.entity.PaymentProvider;
import com.app.ecommerce.payment.entity.PaymentStatus;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PaymentResponse(String transactionId, PaymentStatus paymentStatus, BigDecimal amount,
                              PaymentProvider paymentProvider) {
}
