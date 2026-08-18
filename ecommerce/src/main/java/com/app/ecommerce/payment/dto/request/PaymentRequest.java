package com.app.ecommerce.payment.dto.request;

import com.app.ecommerce.payment.entity.PaymentProvider;

public record PaymentRequest(PaymentProvider paymentProvider) {
}
