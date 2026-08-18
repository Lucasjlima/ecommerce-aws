package com.app.ecommerce.payment.gateway;

import com.app.ecommerce.payment.dto.response.PaymentResult;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class StripeGateway implements PaymentGateway {
    @Override
    public PaymentResult charge(BigDecimal amount, String cardToken) {
        return new PaymentResult(true, UUID.randomUUID());
    }
}
