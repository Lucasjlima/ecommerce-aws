package com.app.ecommerce.messaging.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultJacksonJavaTypeMapper;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {

    @Value("${app.rabbitmq.email.queue}")
    private String emailQueue;

    @Value("${app.rabbitmq.invoice.queue}")
    private String invoiceQueue;

    @Value("${app.rabbitmq.order.exchange}")
    private String orderExchange;

    @Value("${app.rabbitmq.order.paid}")
    private String orderPaidRoutingKey;

    @Value("${app.rabbitmq.dead-letter.exchange}")
    private String dlxExchange;

    @Value("${app.rabbitmq.order.dead-letter.routing-key}")
    private String dlqRoutingKey;


    //CRIAÇÃO DO EXCHANGE (TÓPICO)
    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(orderExchange, true, false);
    }

    //CRIAÇÃO DAS FILAS (QUEUES) E CONFIGURAÇÃO DE DEAD LETTER EXCHANGE

    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(emailQueue)
                .withArgument("x-dead-letter-exchange", dlxExchange)
                .withArgument("x-dead-letter-routing-key", dlqRoutingKey)
                .build();
    }

    @Bean
    public Queue invoiceQueue() {
        return QueueBuilder.durable(invoiceQueue)
                .withArgument("x-dead-letter-exchange", dlxExchange)
                .withArgument("x-dead-letter-routing-key", dlqRoutingKey)
                .build();
    }


    //CRIACAO DOS BINDINGS (CONECTA QUEUE COM EXCHANGE)

    @Bean
    public Binding emailBinding(Queue emailQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(emailQueue).to(orderExchange).with(orderPaidRoutingKey);
    }

    @Bean
    public Binding invoiceBinding(Queue invoiceQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(invoiceQueue).to(orderExchange).with(orderPaidRoutingKey);
    }

    @Bean
    public JacksonJsonMessageConverter jsonMessageConverter() {

        JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter();
        DefaultJacksonJavaTypeMapper typeMapper = new DefaultJacksonJavaTypeMapper();

        typeMapper.addTrustedPackages("com.app.ecommerce.messaging.dto");
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, JacksonJsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }
}
