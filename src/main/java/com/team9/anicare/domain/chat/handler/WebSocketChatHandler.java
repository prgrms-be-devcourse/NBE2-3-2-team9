package com.team9.anicare.domain.chat.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketChatHandler extends TextWebSocketHandler {

    private static final String USER_STATUS_KEY = "user_status"; // Redis 키 상수화
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * WebSocket 연결 종료 처리
     *
     * @param session WebSocket 세션
     * @param status  연결 종료 상태
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        try {
            String userId = session.getId();
            updateUserStatus(userId, "disconnected");
            log.info("User {} disconnected. Status: {}", userId, status);
        } catch (Exception e) {
            log.error("Error during connection closure: {}", e.getMessage(), e);
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        try {
            String userId = session.getId();
            updateUserStatus(userId, "connected");
            log.info("User {} connected.", userId);
        } catch (Exception e) {
            log.error("Error during connection establishment: {}", e.getMessage(), e);
        }
    }

    /**
     * Redis에서 사용자 상태 조회
     *
     * @param userId 사용자 ID
     * @return 사용자 상태
     */
    public String getUserStatus(String userId) {
        try {
            return (String) redisTemplate.opsForHash().get(USER_STATUS_KEY, userId);
        } catch (Exception e) {
            log.error("Failed to fetch user status for ID {}: {}", userId, e.getMessage(), e);
            return "unknown";
        }
    }

    /**
     * 사용자 상태를 Redis에 업데이트
     *
     * @param userId 사용자 ID
     * @param status 사용자 상태 (connected/disconnected)
     */
    private void updateUserStatus(String userId, String status) {
        try {
            redisTemplate.opsForHash().put(USER_STATUS_KEY, userId, status);
        } catch (Exception e) {
            log.error("Failed to update user status for ID {}: {}", userId, e.getMessage(), e);
        }
    }
}
