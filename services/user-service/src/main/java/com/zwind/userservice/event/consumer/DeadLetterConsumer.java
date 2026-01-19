package com.zwind.userservice.event.consumer;

import com.zwind.userservice.constants.RabbitMQConstantConfig;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeadLetterConsumer {
    @RabbitListener(queues = RabbitMQConstantConfig.USER_DEAD_LETTER_QUEUE)
    public void processDeadLetter(Message failedMessage) {
        String originalQueue = failedMessage.getMessageProperties().getConsumerQueue();
        String reason = (String) failedMessage.getMessageProperties().getHeaders().get("x-first-death-reason");

        log.error("!!!! FOUND DEAD LETTER !!!!");
        log.error(":::: queue: {}", originalQueue);
        log.error(":::: reason: {}", reason);
        log.error(":::: content: {}", new String(failedMessage.getBody()));
    }
}
