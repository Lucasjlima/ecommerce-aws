package com.app.ecommerce.email.service;

import com.app.ecommerce.email.dto.PurchaseConfirmation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmtpEmailService implements EmailService {
    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String from;

    @Override
    public void sendPurchaseNotification(PurchaseConfirmation purchaseConfirmation) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(purchaseConfirmation.userEmail());
        message.setSubject("Purchase Confirmation");
        message.setText("Hello " + purchaseConfirmation.userName() + "\n\n" +
                "Your order with ID " + purchaseConfirmation.orderId() +
                " and Total Amount " + purchaseConfirmation.totalAmount() + " has been paid.");
        mailSender.send(message);
    }
}
