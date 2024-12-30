package com.team9.anicare.chat.service;

import com.team9.anicare.chat.dto.ChatMessageDTO;
import com.team9.anicare.chat.dto.ChatRoomDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChatLogService {

    private final RedisTemplate<String, ChatMessageDTO> redisTemplate; // Redis와의 상호작용을 위한 템플릿
    private static final long CHAT_LOG_TTL_SECONDS = 30 * 24 * 60 * 60; // 30일

    /**
     * Redis에 채팅 메시지 저장
     * @param roomId 채팅방 ID
     * @param chatMessage 저장할 메시지 내용
     */
    public void saveChatMessage(String roomId, ChatMessageDTO chatMessage) {
        String key = "chat_logs:" + roomId;
        String hashKey = chatMessage.getSender() + ":" + System.currentTimeMillis();

        // 메시지의 timestamp 설정
        if (chatMessage.getTimestamp() == null) {
            chatMessage.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        // Hash에 저장
        redisTemplate.opsForHash().put(key, hashKey, chatMessage);

        // TTL 설정
        setTTL(key);
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