package com.team9.anicare.domain.chat.config;

import com.team9.anicare.domain.auth.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


/**
 * StompChannelInterceptor
 * - WebSocket 연결 시 JWT 인증을 처리합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StompChannelInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) {
            return message;
        }

        StompCommand command = accessor.getCommand();

        // WebSocket 연결 및 메시지 전송 시 JWT 토큰 검증
        if (StompCommand.CONNECT.equals(command) || StompCommand.SEND.equals(command)) {
            String token = accessor.getFirstNativeHeader("Authorization");

            // 토큰 유효성 검증
            if (!StringUtils.hasText(token) || !jwtTokenProvider.validateToken(token)) {
                log.error("WebSocket 연결 실패: 유효하지 않은 JWT 토큰");
                throw new IllegalArgumentException("Invalid or missing JWT Token");
            }

            // 토큰에서 사용자 ID 추출 및 세션에 저장
            String userId = String.valueOf(jwtTokenProvider.getId(token));
            accessor.setUser(() -> userId);

            log.info("WebSocket 인증 성공: 사용자 ID = {}", userId);
        }

        return message;
    }
}
