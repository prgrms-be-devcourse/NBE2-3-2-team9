package com.team9.anicare.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team9.anicare.chat.dto.ChatMessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * RedisMessagePublisher
 * - Redis Pub/Sub 채널로 메시지를 발행합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisMessagePublisher {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final ChatLogService chatLogService;
    private static final int MAX_RETRIES = 3; // 최대 재시도 횟수
    private static final long RETRY_DELAY_MS = 1000; // 재시도 간격 (밀리초)

    /**
     * Redis 채널로 메시지 발행
     * @param channel 메시지를 발행할 Redis 채널
     * @param message 발행할 메시지 (JSON 형식)
     */
    public void publish(String channel, String message) {
        int attempt = 0;
        while (attempt < MAX_RETRIES) {
            try {
                redisTemplate.convertAndSend(channel, message);
                log.debug("Published message to channel: {}", channel);

                ChatMessageDTO chatMessage = objectMapper.readValue(message, ChatMessageDTO.class);
                log.debug("Saving chat message: {}", chatMessage);

                chatLogService.saveChatMessage(chatMessage.getRoomId(), chatMessage); // 저장 호출
            // 성공 시 메서드 종료
            } catch (Exception e) {
                attempt++;
                if (attempt >= MAX_RETRIES) {
                    throw new RuntimeException("Redis 메시지 발행 실패: " + e.getMessage(), e);
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(RETRY_DELAY_MS); // 재시도 전 대기
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("재시도 대기 중 인터럽트 발생", interruptedException);
                }
            }
        }
    }
}
