package com.app.ecommerce.email.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PurchaseConfirmation(UUID orderId, String userName, String userEmail, BigDecimal totalAmount) {
}
