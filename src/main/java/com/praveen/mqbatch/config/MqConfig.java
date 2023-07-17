package com.praveen.mqbatch.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqConfig {

  @Bean
  public Queue queue() {
    return new Queue("employees_queue");
  }

  @Bean
  public TopicExchange exchange() {
    return new TopicExchange("employees_exchange");
  }

  @Bean
  public Binding binding(Queue queue, TopicExchange exchange) {
    return BindingBuilder.bind(queue).to(exchange).with("employees_routingKey");
  }

  @Bean
  public MessageConverter converter() {
    return new Jackson2JsonMessageConverter();
  }

  @Bean
  public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
    final RabbitTemplate rabbit = new RabbitTemplate(connectionFactory);
    rabbit.setRoutingKey("employees_routingKey");
    rabbit.setExchange("employees_exchange");
    rabbit.setMessageConverter(converter());
    return rabbit;
  }
}
