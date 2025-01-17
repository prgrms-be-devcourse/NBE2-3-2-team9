package com.team9.anicare.domain.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserSessionService {

    private static final String USER_STATUS_KEY = "user_status";
    private static final String STATUS_CONNECTED = "connected";
    private static final String STATUS_DISCONNECTED = "disconnected";

    private final UserStatusNotifier userStatusNotifier;
    private final RedisTemplate<String, String> redisTemplate;


    /**
     * 사용자가 연결되었을 때 상태 업데이트 및 실시간 전송
     */
    public void setUserConnected(String userId)
    {
        updateUserStatus(userId, STATUS_CONNECTED);
        userStatusNotifier.notifyUserStatusChange(userId, STATUS_CONNECTED);
    }


    /**
     * 사용자가 연결 해제되었을 때 상태 업데이트 및 실시간 전송
     */
    public void setUserDisconnected(String userId)
    {
        deleteUserStatus(userId);  // 🔥 Redis에서 삭제
        userStatusNotifier.notifyUserStatusChange(userId, STATUS_DISCONNECTED);
    }


    /**
     * Redis에 사용자 상태 업데이트
     */
    private void updateUserStatus(String userId, String status)
    {
        try
        {
            redisTemplate.opsForHash().put(USER_STATUS_KEY, userId, status);
        }
        catch (Exception e)
        {
            log.error("사용자 상태 업데이트 실패 ID {}: {}", userId, e.getMessage(), e);
        }
    }



    /**
     * Redis에서 사용자 상태 삭제
     */
    private void deleteUserStatus(String userId) {
        try {
            redisTemplate.opsForHash().delete(USER_STATUS_KEY, userId);
            log.info("Redis에서 사용자 상태 삭제됨: {}", userId);
        } catch (Exception e) {
            log.error("Redis 상태 삭제 실패: 사용자 ID = {}", userId, e);
        }
    }


    /**
     *  Ping을 받을 때마다 Redis TTL을 갱신하는 메서드
     */
    public void refreshUserStatus(String userId) {
        try {
            redisTemplate.expire(USER_STATUS_KEY, Duration.ofMinutes(1));  // TTL 1분 설정
            log.info("TTL 갱신: 사용자 ID = {}", userId);
        } catch (Exception e) {
            log.error("TTL 갱신 실패: 사용자 ID = {}", userId, e);
        }
    }
}
