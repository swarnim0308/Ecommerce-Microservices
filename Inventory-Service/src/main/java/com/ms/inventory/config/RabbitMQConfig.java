package com.ms.inventory.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

	@Value("${app.rabbitmq.queue:inventory.order.placed}")
	private String queue;

	@Bean
	public Queue inventoryQueue() {
		return new Queue(queue, true);
	}

	@Bean
	public MessageConverter jsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
			ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
		factory.setMessageConverter(jsonMessageConverter);
		return factory;
	}
}
