package com.team9.anicare.domain.chat.handler;

import com.team9.anicare.domain.chat.service.UserSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketChatHandler extends TextWebSocketHandler {

    private final UserSessionService userSessionService;

    /**
     * WebSocket 연결 성립 시 처리
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String userId = session.getId();
        userSessionService.setUserConnected(userId);
        log.info("User {} connected.", userId);
    }

    /**
     * WebSocket 연결 종료 시 처리
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String userId = session.getId();
        userSessionService.setUserDisconnected(userId);
        log.info("User {} disconnected. Status: {}", userId, status);
    }

    /**
     * 메시지 수신 처리
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String userId = session.getId();
        String payload = message.getPayload();
        log.info("Received message from {}: {}", userId, payload);

        // Echo Message (테스트용)
        try {
            session.sendMessage(new TextMessage("Echo: " + payload));
        } catch (Exception e) {
            log.error("Error sending message to {}: {}", userId, e.getMessage());
        }
    }
}
