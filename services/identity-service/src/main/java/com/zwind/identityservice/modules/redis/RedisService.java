package com.zwind.identityservice.modules.redis;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RedisService {
    RedisTemplate<String, Object> template;
    ObjectMapper redisObjectMapper;

    public <T> void setKey(String key, T value, int ttl, TimeUnit timeUnit) {
        template.opsForValue().set(key, value, ttl, timeUnit);
    }

    public <T> T getKey(String key, Class<T> clazz){
        Object value = template.opsForValue().get(key);
        if(value == null)
            return null;

        return redisObjectMapper.convertValue(value, clazz);
    }

    public void deleteKey(String key){
        boolean deleted = template.delete(key);
        if(deleted) log.info("Deleted key {}", key);
        else log.error("Cannot deleted key {}", key);
    }

    public void deleteMultipleKey(List<String> keys){
        if (keys == null || keys.isEmpty()) {
            return;
        }
        template.delete(keys);
    }
}
