package com.app.ecommerce.email.service;

import com.app.ecommerce.email.dto.PurchaseConfirmation;

public interface EmailService {
    void sendPurchaseNotification(PurchaseConfirmation purchaseConfirmation);
}
