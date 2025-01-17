package com.team9.anicare.domain.chat.config;

import com.team9.anicare.domain.chat.service.UserSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;


@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final UserSessionService userSessionService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event)
    {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal userPrincipal = headerAccessor.getUser();

        if (userPrincipal != null)
        {
            String userId = userPrincipal.getName();
            log.info("사용자 연결됨: " + userId);
            userSessionService.setUserConnected(userId);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event)
    {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal userPrincipal = headerAccessor.getUser();

        if (userPrincipal != null)
        {
            String userId = userPrincipal.getName();
            log.info("사용자 연결 해제됨: " + userId);
            userSessionService.setUserDisconnected(userId);
        }
    }
}
