package com.app.ecommerce.messaging.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InvoiceGenerationListener {

    //INJETAR O SERVICE AQUI NO FUTURO

    @RabbitListener(queues = "${app.rabbitmq.invoice.queue}")
    public void onInvoiceGeneration() {
        //METODO DO SERVICE E PUBLISH AQUI DENTRO.
    }
}
