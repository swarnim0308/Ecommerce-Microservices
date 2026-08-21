package com.retail.shippingservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${app.rabbitmq.exchange:order.exchange}")
    private String exchange;

    @Value("${app.rabbitmq.routing-key:order.placed}")
    private String routingKey;

    @Value("${app.rabbitmq.queue:inventory.order.placed}")
    private String queue;

    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(exchange);
    }

    @Bean
    public Queue inventoryQueue() {
        return new Queue(queue, true);
    }

    @Bean
    public Binding binding(Queue inventoryQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(inventoryQueue).to(orderExchange).with(routingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }
}
