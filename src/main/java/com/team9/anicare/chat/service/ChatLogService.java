package com.team9.anicare.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatLogService {
    private final StringRedisTemplate redisTemplate;

    // 채팅 로그 저장
    public void saveChatLog(String roomId, String message) {
        redisTemplate.opsForList().rightPush("chat_logs:" + roomId, message);
    }

    // 채팅 로그 조회
    public List<String> getChatLogs(String roomId) {
        return redisTemplate.opsForList().range("chat_logs:" + roomId, 0, -1);
    }
}
