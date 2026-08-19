package com.app.ecommerce;

import com.app.ecommerce.email.dto.PurchaseConfirmation;
import com.app.ecommerce.email.service.EmailService;
import com.app.ecommerce.messaging.dto.OrderPaidEvent;
import com.app.ecommerce.messaging.listener.EmailNotificationListener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EmailNotificationListenerTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private EmailNotificationListener listener;

    @Test
    void shouldMapEventAndDelegateToEmailService() {
        //Arrange
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        OrderPaidEvent event = new OrderPaidEvent(
                orderId,
                userId,
                "john.doe@example.com",
                "John Doe",
                List.of(),
                BigDecimal.valueOf(149.90)
        );

        //Act
        listener.onEmailNotification(event);

        //Assert
        ArgumentCaptor<PurchaseConfirmation> captor = ArgumentCaptor.forClass(PurchaseConfirmation.class);
        verify(emailService).sendPurchaseNotification(captor.capture());

        PurchaseConfirmation confirmation = captor.getValue();

        // Guarda a pegadinha de ordem: userName e userEmail NAO podem estar trocados
        assertEquals(orderId, confirmation.orderId());
        assertEquals("John Doe", confirmation.userName());
        assertEquals("john.doe@example.com", confirmation.userEmail());
        assertEquals(BigDecimal.valueOf(149.90), confirmation.totalAmount());
    }
}
