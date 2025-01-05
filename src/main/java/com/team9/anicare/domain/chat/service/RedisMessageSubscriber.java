package com.team9.anicare.domain.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team9.anicare.domain.chat.dto.ChatMessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Slf4j
@RequiredArgsConstructor
public class RedisMessageSubscriber {

    private final ObjectMapper objectMapper; // 메시지 역직렬화를 위한 ObjectMapper
    private final SimpMessagingTemplate messagingTemplate; // WebSocket 메시지 전송용
    private final ChatLogService chatLogService; // Redis에 메시지를 저장하기 위한 서비스

    /**
     * Redis Pub/Sub에서 메시지를 수신했을 때 호출되는 메서드
     * @param message 수신된 메시지 (JSON 형식)
     */
    public void handleMessage(String message) {
        try {
            ChatMessageDTO chatMessage = deserializeMessage(message);  // 메시지를 역직렬화하여 ChatMessageDTO로 변환
            sendToWebSocket(chatMessage);                              // WebSocket으로 메시지 전송
            saveToRedis(chatMessage);                                  // Redis에 메시지 저장
        } catch (JsonProcessingException e) {
            log.error("JSON 역직렬화 중 오류 발생: {}", e.getMessage());
        } catch (Exception e) {
            log.error("WebSocket 전송 또는 Redis 저장 중 오류 발생: {}", e.getMessage());
        }
    }

    /**
     * JSON 메시지를 ChatMessageDTO로 변환
     * @param message JSON 형식의 메시지
     * @return ChatMessageDTO 객체
     * @throws JsonProcessingException JSON 역직렬화 실패 시 예외 발생
     */
    private ChatMessageDTO deserializeMessage(String message) throws JsonProcessingException {
        return objectMapper.readValue(message, ChatMessageDTO.class);
    }

    /**
     * WebSocket으로 메시지 전송
     * @param chatMessage 전송할 메시지 객체
     */
    private void sendToWebSocket(ChatMessageDTO chatMessage) {
        try {
            messagingTemplate.convertAndSend("/topic/chat/" + chatMessage.getRoomId(), chatMessage);
            log.info("WebSocket으로 메시지 전송 완료: RoomID={}, Sender={}", chatMessage.getRoomId(), chatMessage.getSender());
        } catch (Exception e) {
            log.error("WebSocket 전송 실패: RoomID={}, Sender={}, Error={}", chatMessage.getRoomId(), chatMessage.getSender(), e.getMessage());
        }
    }

    /**
     * Redis에 메시지 저장
     * @param chatMessage 저장할 메시지 객체
     */
    private void saveToRedis(ChatMessageDTO chatMessage) {
        try {
            chatLogService.saveChatMessage(chatMessage.getRoomId(), chatMessage);
            log.info("Redis에 메시지 저장 완료: RoomID={}, Sender={}", chatMessage.getRoomId(), chatMessage.getSender());
        } catch (Exception e) {
            log.error("Redis 메시지 저장 실패: RoomID={}, Error={}", chatMessage.getRoomId(), e.getMessage());
        }
    }
}