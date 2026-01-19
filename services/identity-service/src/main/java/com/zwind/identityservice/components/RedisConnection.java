package com.zwind.identityservice.components;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RedisConnection
        implements ApplicationListener<ApplicationReadyEvent> {
    private final LettuceConnectionFactory factory;

    public RedisConnection(LettuceConnectionFactory factory) {
        this.factory = factory;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try (var connection = factory.getConnection()) {
            log.info("Redis connected successfully, PING = {}", connection.ping());
        } catch (Exception e) {
            log.error("Cannot connect to Redis", e);
            log.error(e.getMessage());
        }
    }
}
