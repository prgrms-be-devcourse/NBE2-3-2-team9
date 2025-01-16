package com.team9.anicare.domain.chat.handler;

import com.team9.anicare.domain.auth.security.JwtTokenProvider;
import com.team9.anicare.domain.chat.service.UserSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketChatHandler extends TextWebSocketHandler {

    private final UserSessionService userSessionService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * WebSocket 연결 성립 시 처리
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String token = getTokenFromSession(session);

        if (!StringUtils.hasText(token) || !jwtTokenProvider.validateToken(token)) {
            log.error("WebSocket 연결 실패: 유효하지 않은 JWT 토큰");
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        // 토큰에서 사용자 ID 추출
        String userId = String.valueOf(jwtTokenProvider.getId(token));

        // 사용자 연결 상태 저장
        userSessionService.setUserConnected(userId);

        // 사용자 ID를 세션에 저장
        session.getAttributes().put("userId", userId);

        log.info("WebSocket 연결 성공: 사용자 ID = {}", userId);
    }


    /**
     * WebSocket 연결 종료 시 처리
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = (String) session.getAttributes().get("userId");

        if (StringUtils.hasText(userId)) {
            userSessionService.setUserDisconnected(userId);
            log.info("WebSocket 연결 종료: 사용자 ID = {}, 상태 = {}", userId, status);
        }
    }


    /**
     * 메시지 수신 처리
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String userId = (String) session.getAttributes().get("userId");

        if (userId == null) {
            log.error("메시지 수신 실패: 인증되지 않은 사용자");
            session.close(CloseStatus.NOT_ACCEPTABLE);
            return;
        }

        String payload = message.getPayload();
        log.info("메시지 수신 - 사용자 ID: {}, 내용: {}", userId, payload);

        // Echo Message (테스트용)
        session.sendMessage(new TextMessage("Echo: " + payload));
    }


    /**
     * 세션에서 JWT 토큰 추출
     */
    private String getTokenFromSession(WebSocketSession session) {
        Map<String, Object> attributes = session.getAttributes();
        String token = (String) attributes.get("Authorization");

        // 클라이언트에서 전송한 헤더에서 토큰 추출
        if (!StringUtils.hasText(token)) {
            token = session.getUri().getQuery();  // 쿼리스트링에서 토큰 추출 (예: ?Authorization=token)
            if (StringUtils.hasText(token) && token.startsWith("Authorization=")) {
                token = token.substring("Authorization=".length());
            }
        }

        return token;
    }
}
