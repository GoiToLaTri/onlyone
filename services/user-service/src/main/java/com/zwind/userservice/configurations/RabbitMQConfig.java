package com.zwind.userservice.configurations;

import com.zwind.userservice.constants.RabbitMQConstantConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitMQConfig {

    @Bean
    DirectExchange userDeadLetterExchange() {
        return new DirectExchange(RabbitMQConstantConfig.USER_DEAD_LETTER_EXCHANGE);
    }

    @Bean
    Queue userDeadLetterQueue() {
        return new Queue(RabbitMQConstantConfig.USER_DEAD_LETTER_QUEUE, true);
    }

    @Bean
    Binding userDQLBinding() {
        return BindingBuilder.bind(userDeadLetterQueue())
                .to(userDeadLetterExchange())
                .with(RabbitMQConstantConfig.USER_DEAD_RK);
    }
    @Bean
    Queue createProfileQueue(){
        return QueueBuilder.durable(RabbitMQConstantConfig.USER_CREATE_PROFILE_QUEUE)
                .withArgument("x-dead-letter-exchange", RabbitMQConstantConfig.USER_DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", RabbitMQConstantConfig.USER_DEAD_RK)
                .build();
    }

    @Bean
    TopicExchange createProfileExchange() {
        return new TopicExchange(RabbitMQConstantConfig.IDENTITY_SERVICE_EXCHANGE);
    }

    @Bean
    TopicExchange profileExchange() {
        return new TopicExchange(RabbitMQConstantConfig.PROFILE_EVENTS_EXCHANGE);
    }

    @Bean
    Binding createProfileBinding(Queue createProfileQueue, TopicExchange createProfileExchange) {
        return BindingBuilder.bind(createProfileQueue)
                .to(createProfileExchange).with(RabbitMQConstantConfig.USER_CREATED_RK);
    }

    @Bean
    public JacksonJsonMessageConverter producerJackson2MessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
