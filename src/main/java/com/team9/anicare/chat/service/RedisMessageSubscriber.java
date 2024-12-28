package com.team9.anicare.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team9.anicare.chat.dto.ChatMessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisMessageSubscriber implements MessageListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, ChatMessageDTO> redisTemplate;  // 이 부분 맞게 한건지 다시 확인하기

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            // Redis에서 메시지 수신
            ChatMessageDTO chatMessage = objectMapper.readValue(message.getBody(), ChatMessageDTO.class);

            // WebSocket을 통해 메시지 전송
            messagingTemplate.convertAndSend("/topic/chat/" + chatMessage.getRoomId(), chatMessage);

            // 메시지를 Redis에 저장 (키워드 검색 가능하도록 저장)
            redisTemplate.opsForHash().put(
                    "chat_message:" + chatMessage.getRoomId(),
                    chatMessage.getSender() + ":" + System.currentTimeMillis(),
                    chatMessage.getContent()
            );

            // 디버그 로그
            log.debug("Redis 메시지 수신 및 WebSocket 브로드캐스트: {}", chatMessage);
        } catch (Exception e) {
            log.error("Redis 메시지 처리 중 오류 발생: {}", e.getMessage());
        }
    }
}
