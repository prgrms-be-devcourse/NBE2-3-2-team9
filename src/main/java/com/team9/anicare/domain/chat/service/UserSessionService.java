package com.team9.anicare.domain.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSessionService {

    private static final String USER_STATUS_KEY = "user_status";
    private static final String STATUS_CONNECTED = "connected";
    private static final String STATUS_DISCONNECTED = "disconnected";

    private final RedisTemplate<String, String> redisTemplate;

    public void setUserConnected(String userId) {
        updateUserStatus(userId, STATUS_CONNECTED);
    }

    public void setUserDisconnected(String userId) {
        updateUserStatus(userId, STATUS_DISCONNECTED);
    }

    public String getUserStatus(String userId) {
        try {
            return (String) redisTemplate.opsForHash().get(USER_STATUS_KEY, userId);
        } catch (Exception e) {
            log.error("Failed to fetch user status for ID {}: {}", userId, e.getMessage(), e);
            return "unknown";
        }
    }

    private void updateUserStatus(String userId, String status) {
        try {
            redisTemplate.opsForHash().put(USER_STATUS_KEY, userId, status);
        } catch (Exception e) {
            log.error("Failed to update user status for ID {}: {}", userId, e.getMessage(), e);
        }
    }
}
