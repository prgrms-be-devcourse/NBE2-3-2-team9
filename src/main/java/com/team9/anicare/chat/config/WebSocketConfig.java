package com.team9.anicare.chat.config;

import com.team9.anicare.auth.security.JwtTokenProvider;
import com.team9.anicare.chat.handler.WebSocketChatHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocketConfig 클래스는 WebSocket 메시지 브로커를 구성합니다.
 * - STOMP 프로토콜을 사용하여 WebSocket 연결을 설정합니다.
 * - 클라이언트와 서버 간의 메시지 교환 경로를 정의합니다.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtTokenProvider jwtTokenProvider;

    public WebSocketConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * STOMP 엔드포인트를 등록하여 클라이언트가 WebSocket에 연결할 수 있도록 설정합니다.
     *
     * @param registry StompEndpointRegistry 객체
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 클라이언트 연결을 위한 WebSocket 엔드포인트 설정
        registry.addEndpoint("/chat-socket")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    /**
     * 메시지 브로커 구성을 설정합니다.
     * - 메시지 구독 및 송신 경로를 정의합니다.
     *
     * @param registry MessageBrokerRegistry 객체
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 클라이언트가 메시지를 구독하는 경로
        registry.enableSimpleBroker("/topic/");
        // 클라이언트가 메시지를 서버로 보내는 경로
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new StompChannelInterceptor(jwtTokenProvider));
    }
}
