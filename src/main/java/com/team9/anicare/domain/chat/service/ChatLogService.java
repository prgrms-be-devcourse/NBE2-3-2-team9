package com.team9.anicare.domain.chat.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatLogService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final long CHAT_LOG_TTL_SECONDS = 30 * 24 * 60 * 60; // 30일

    @PostConstruct
    public void setupRedisTemplate() {
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(ChatMessageDTO.class));
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(ChatMessageDTO.class));
    }

    /**
     * Redis에 채팅 메시지 저장
     * @param roomId 채팅방 ID
     * @param chatMessage 저장할 메시지 내용
     */
    public void saveChatMessage(String roomId, ChatMessageDTO chatMessage) {
        String key = "chat_logs:" + roomId;
        String hashKey = chatMessage.getSender() + ":" + System.currentTimeMillis();

        try {
            chatMessage.setTimestamp(java.time.LocalDateTime.now().toString());
            redisTemplate.opsForHash().put(key, hashKey, chatMessage); // 저장
            setTTL(key); // TTL 설정
            log.debug("Message saved to Redis: key={}, hashKey={}, value={}", key, hashKey, chatMessage);
        } catch (Exception e) {
            log.error("Failed to save chat message to Redis: {}", e.getMessage());
        }

    }

    /**
     * Redis에 TTL 설정
     * @param key Redis 키
     */
    private void setTTL(String key) {
        redisTemplate.expire(key, Duration.ofSeconds(CHAT_LOG_TTL_SECONDS));
    }

    /**
     * Redis에서 채팅 로그 조회
     * @param roomId 채팅방 ID
     * @return 채팅 메시지 리스트
     */
    public List<ChatMessageDTO> getChatLogs(String roomId) {
        String key = "chat_logs:" + roomId;
        return redisTemplate.opsForHash().values(key).stream()
                .map(value -> (ChatMessageDTO) value)
                .toList(); // Hash의 모든 값을 조회 후 변환
    }

    public void saveChatRoomMetadata(ChatRoomDTO chatRoom) {
        String key = "chat_room_metadata";
        redisTemplate.opsForHash().put(key, chatRoom.getRoomId() + ":name", chatRoom.getRoomName());
        redisTemplate.opsForHash().put(key, chatRoom.getRoomId() + ":description", chatRoom.getDescription());
    }

    public List<String> searchChatRoomsByKeyword(String keyword) {
        List<String> matchingRooms = new ArrayList<>();

        Cursor<Map.Entry<Object, Object>> roomCursor = redisTemplate.opsForHash()
                .scan("chat_room_metadata", ScanOptions.scanOptions().match("*" + keyword + "*").build());

        while (roomCursor.hasNext()) {
            Map.Entry<Object, Object> entry = roomCursor.next();
            String roomId = entry.getKey().toString().split(":")[0];
            matchingRooms.add(roomId);
        }

        for (String roomId : getAllChatRoomIds()) {
            String key = "chat_logs:" + roomId;
            Cursor<Map.Entry<Object, Object>> messageCursor = redisTemplate.opsForHash()
                    .scan(key, ScanOptions.scanOptions().match("*" + keyword + "*").build());

            while (messageCursor.hasNext()) {
                Map.Entry<Object, Object> entry = messageCursor.next();
                if (entry.getValue() instanceof ChatMessageDTO message) {
                    matchingRooms.add(message.getRoomId());
                }
            }
        }

        return matchingRooms.stream().distinct().toList();
    }

    private List<String> getAllChatRoomIds() {
        Set<Object> keys = redisTemplate.opsForHash().keys("chat_room_metadata");
        return keys.stream()
                .map(key -> key.toString().split(":")[0])
                .distinct()
                .toList();
    }
}