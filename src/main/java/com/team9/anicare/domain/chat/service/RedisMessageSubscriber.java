package com.team9.anicare.domain.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team9.anicare.domain.chat.dto.ChatMessageResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * RedisMessageSubscriber
 * - Redis에서 수신한 메시지를 WebSocket으로 전달
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisMessageSubscriber {

    private final ObjectMapper objectMapper;               // JSON 역직렬화
    private final SimpMessagingTemplate messagingTemplate; // WebSocket 메시지 전송

    /**
     * Redis로부터 수신한 메시지 처리
     *
     * @param message Redis에서 수신한 JSON 메시지
     */
    public void handleMessage(String message) {
        try {
            // 메시지 역직렬화
            ChatMessageResponseDTO chatMessage = objectMapper.readValue(message, ChatMessageResponseDTO.class);

            // WebSocket으로 전송
            messagingTemplate.convertAndSend("/topic/chat/" + chatMessage.getMessageId(), chatMessage);
            log.info("WebSocket으로 메시지 전송: RoomID={}, Content={}", chatMessage.getMessageId(), chatMessage.getContent());

        } catch (Exception e) {
            log.error("Redis 메시지 처리 중 오류 발생: {}", e.getMessage());
        }
    }
}