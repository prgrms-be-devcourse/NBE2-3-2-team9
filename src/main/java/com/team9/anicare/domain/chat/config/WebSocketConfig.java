package com.team9.anicare.domain.chat.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocketConfig 클래스
 * - STOMP 기반 WebSocket 메시지 브로커 설정
 */
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * 클라이언트 연결을 위한 WebSocket 엔드포인트 등록
     *
     * @param registry STOMP 엔드포인트 레지스트리
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 클라이언트 연결을 위한 WebSocket 엔드포인트 설정
        registry.addEndpoint("/chat-socket")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    /**
     * 메시지 브로커 설정
     * - /topic 경로는 클라이언트가 메시지를 구독하는 경로
     * - /app 경로는 클라이언트가 서버로 메시지를 송신하는 경로
     *
     * @param registry 메시지 브로커 레지스트리
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 클라이언트가 메시지를 구독하는 경로
        registry.enableSimpleBroker("/topic/");
        // 클라이언트가 메시지를 서버로 보내는 경로
        registry.setApplicationDestinationPrefixes("/app");
    }


    /**
     * 클라이언트 Inbound 채널 인터셉터 등록
     * - StompChannelInterceptor를 사용하여 JWT 인증 처리
     *
     * @param registration 채널 등록 객체
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // registration.interceptors(new StompChannelInterceptor(jwtTokenProvider));
    }
}
