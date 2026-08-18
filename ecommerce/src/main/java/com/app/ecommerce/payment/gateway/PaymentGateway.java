package com.app.ecommerce.payment.gateway;

import com.app.ecommerce.payment.dto.response.PaymentResult;

import java.math.BigDecimal;

public interface PaymentGateway {
    PaymentResult charge(BigDecimal amount, String cardToken);
}
