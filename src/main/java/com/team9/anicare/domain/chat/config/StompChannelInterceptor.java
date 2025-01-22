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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;


/**
 * StompChannelInterceptor
 * - WebSocket 연결 시 JWT 인증을 처리합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StompChannelInterceptor implements ChannelInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // 메시지에서 StompHeaderAccessor를 가져옴
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        // accessor가 null이거나 CONNECT 명령이 아니면 메시지를 그대로 반환
        if (accessor == null || !StompCommand.CONNECT.equals(accessor.getCommand())) {
            return message;
        }

        // Authorization 헤더에서 JWT 토큰 추출
        String token = Optional.ofNullable(accessor.getFirstNativeHeader(AUTHORIZATION_HEADER))
                .filter(header -> header.startsWith(BEARER_PREFIX))
                .map(header -> header.substring(BEARER_PREFIX.length()))
                .orElseThrow(() -> {
                    log.error("WebSocket 연결 실패: Authorization 헤더가 없거나 유효하지 않음");
                    return new IllegalArgumentException("Invalid or missing JWT Token");
                });

        // JWT 토큰 유효성 검증
        if (!jwtTokenProvider.validateToken(token)) {
            log.error("WebSocket 연결 실패: 유효하지 않은 JWT 토큰");
            throw new IllegalArgumentException("Invalid or expired JWT Token");
        }

        // JWT 토큰에서 사용자 ID를 추출하고 Authentication 객체 생성
        Long userId = jwtTokenProvider.getId(token);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userId, null, null);
        accessor.setUser(authentication); // 사용자 인증 정보를 WebSocket 세션에 설정

        log.info("WebSocket 인증 성공: 사용자 ID = {}", userId);

        return message;
    }
}
