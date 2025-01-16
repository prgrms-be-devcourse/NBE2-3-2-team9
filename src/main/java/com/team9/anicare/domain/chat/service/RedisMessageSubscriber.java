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

    private static final int MAX_WEBSOCKET_RETRIES = 3;


    /**
     * Redis로부터 수신한 메시지 처리
     */
    public void handleMessage(String message) {
        try {
            ChatMessageResponseDTO chatMessage = objectMapper.readValue(message, ChatMessageResponseDTO.class);
            sendToWebSocket(chatMessage);
        } catch (Exception e) {
            log.error("Redis 메시지 처리 중 오류 발생: {}", e.getMessage());
        }
    }


    /**
     * WebSocket으로 메시지 전송 (재시도 포함)
     */
    private void sendToWebSocket(ChatMessageResponseDTO chatMessage) {
        int attempt = 0;

        while (attempt < MAX_WEBSOCKET_RETRIES) {
            try {
                messagingTemplate.convertAndSend("/topic/chat/room/" + chatMessage.getRoomId(), chatMessage);
                log.info("WebSocket으로 메시지 전송 성공: RoomID={}, Content={}", chatMessage.getRoomId(), chatMessage.getContent());
                break;
            } catch (Exception e) {
                attempt++;
                log.warn("WebSocket 전송 실패 (시도 {}): {}", attempt, e.getMessage());

                if (attempt >= MAX_WEBSOCKET_RETRIES) {
                    log.error("WebSocket 전송 최종 실패. 메시지 전송 포기: {}", chatMessage.getContent());
                }
            }
        }
    }
}