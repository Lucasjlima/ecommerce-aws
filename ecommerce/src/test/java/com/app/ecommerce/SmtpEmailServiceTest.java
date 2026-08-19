package com.app.ecommerce;

import com.app.ecommerce.email.dto.PurchaseConfirmation;
import com.app.ecommerce.email.service.SmtpEmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SmtpEmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private SmtpEmailService smtpEmailService;

    @Test
    void shouldSendPurchaseNotification() {

        //Arrange
        ReflectionTestUtils.setField(
                smtpEmailService,
                "from",
                "noreply@ecommerce.com"
        );

        UUID orderId = UUID.randomUUID();

        PurchaseConfirmation confirmation =
                new PurchaseConfirmation(
                        orderId,
                        "John Doe",
                        "john.doe@example.com",
                        BigDecimal.valueOf(149.90)
                );

        //Act
        smtpEmailService.sendPurchaseNotification(confirmation);

        //Assert
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        verify(mailSender).send(captor.capture());

        SimpleMailMessage sentMessage = captor.getValue();

        assertEquals(
                "noreply@ecommerce.com",
                sentMessage.getFrom()
        );

        assertArrayEquals(
                new String[]{"john.doe@example.com"},
                sentMessage.getTo()
        );

        assertEquals(
                "Purchase Confirmation",
                sentMessage.getSubject()
        );

        assertEquals(
                "Hello John Doe\n\n" +
                        "Your order with ID " + orderId +
                        " and Total Amount " + BigDecimal.valueOf(149.90) + " has been paid.",
                sentMessage.getText()
        );
    }

    @Test
    void shouldThrowMailSendExceptionWhenSendingEmail() {
        //Arrange
        UUID orderId = UUID.randomUUID();

        PurchaseConfirmation confirmation =
                new PurchaseConfirmation(
                        orderId,
                        "John Doe",
                        "john.doe@example.com",
                        BigDecimal.valueOf(149.90)
                );

        doThrow(new MailSendException("SMTP unavailable"))
                .when(mailSender)
                .send(any(SimpleMailMessage.class));

        //Act and Assert
        assertThrows(
                MailSendException.class,
                () -> smtpEmailService.sendPurchaseNotification(confirmation)
        );
    }
}
