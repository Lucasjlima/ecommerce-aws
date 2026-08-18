package com.app.ecommerce.payment.dto.response;

import java.util.UUID;

public record PaymentResult(boolean success, UUID transactionId) {}
