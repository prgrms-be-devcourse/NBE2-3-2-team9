package com.team9.anicare.domain.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserStatusNotifier {

    private final SimpMessagingTemplate messagingTemplate;

    // 사용자 상태 변경 시 프론트에 실시간 전송
    public void notifyUserStatusChange(String userId, String status)
    {
        messagingTemplate.convertAndSend("/topic/user-status/" + userId, status);
    }
}
