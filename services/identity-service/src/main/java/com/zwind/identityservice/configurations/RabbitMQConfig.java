package com.zwind.identityservice.configurations;

import com.zwind.identityservice.constant.RabbitMQConstantConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitMQConfig {
    @Bean
    Queue profileCreatedStatusQueue() {
        return new Queue(RabbitMQConstantConfig.PROFILE_EVENTS_QUEUE, true);
    }

    @Bean
    TopicExchange profileCreatedStatusExchange() {
        return new TopicExchange(RabbitMQConstantConfig.PROFILE_EVENTS_EXCHANGE);
    }

    @Bean
    TopicExchange identityExchange() {
        return new TopicExchange(RabbitMQConstantConfig.IDENTITY_SERVICE_EXCHANGE);
    }

    @Bean
    Binding profileCreatedStatusBinding(Queue profileCreatedStatusQueue,
                                      TopicExchange profileCreatedStatusExchange) {
        return BindingBuilder.bind(profileCreatedStatusQueue).to(profileCreatedStatusExchange)
                .with(RabbitMQConstantConfig.PROFILE_CREATED_STATUS_RK);
    }

    @Bean
    public JacksonJsonMessageConverter producerJackson2MessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
