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
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            /* 클라이언트에서 전달된 JWT 토큰 검증하는 로직입니다.
             * 추후 이러한 내용도 추가할 수 있다면 주석 해제하고 사용하시면 됩니다.

            String token = accessor.getFirstNativeHeader("Authorization");
            if (token == null || !jwtTokenProvider.validateToken(token)) {
                log.error("WebSocket 연결 실패: 잘못된 JWT 토큰");
                throw new IllegalArgumentException("Invalid JWT Token");
            }
            log.info("WebSocket 연결 성공: 사용자 토큰 유효");

            */
        }
        return message;
    }
}
