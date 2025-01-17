package com.team9.anicare.domain.chat.controller;

import com.team9.anicare.domain.chat.service.UserSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class PingPongController {

    private final UserSessionService userSessionService;

    // 클라이언트에서 /app/ping으로 메시지를 보내면 처리
    @MessageMapping("/ping")
    @SendTo("/topic/pong")
    public String handlePing(StompHeaderAccessor accessor) {

        // 사용자 ID를 가져와서 TTL 갱신
        Principal userPrincipal = accessor.getUser();

        if (userPrincipal != null) {
            String userId = userPrincipal.getName();
            userSessionService.refreshUserStatus(userId);  // TTL 갱신
        }

        // 서버가 Pong 응답을 전송
        return "pong";
    }
}
