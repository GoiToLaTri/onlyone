package com.zwind.userservice.event.producer;

import com.zwind.userservice.event.payload.EventPayload;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RabbitMQProducer {
    RabbitTemplate rabbitTemplate;

    public RabbitMQProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendEvent(String exchange, String routingKey, Object data) {
        EventPayload<Object> payload = EventPayload.builder()
                .eventId(UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 12))
                .pattern(routingKey)
                .timestamp(System.currentTimeMillis())
                .data(data)
                .build();

        rabbitTemplate.convertAndSend(exchange, routingKey, payload);
    }
}
