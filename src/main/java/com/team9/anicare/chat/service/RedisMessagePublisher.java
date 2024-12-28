package com.team9.anicare.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisMessagePublisher {

    private final StringRedisTemplate redisTemplate;

    // Redis 채널로 메시지 발행
    public void publish(String channel, String message) {
        redisTemplate.convertAndSend(channel, message);
    }
}
