package com.team9.anicare.chat.service;


import com.team9.anicare.chat.dto.ChatRoomDTO;
import com.team9.anicare.chat.entity.ChatParticipant;
import com.team9.anicare.chat.repository.ChatParticipantRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private static final String CHAT_ROOM_KEY = "chat_rooms"; // Redis에 저장할 채팅방 키
    private static final long ROOM_EXPIRATION_DAYS = 30; // 채팅방 만료 시간 (30일)

    private final ChatParticipantRepository chatParticipantRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    // Redis 직렬화 설정 (Bean으로 설정하는 것이 더 권장됨)
    @PostConstruct
    public void setupRedisTemplate() {
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(ChatRoomDTO.class));
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(ChatRoomDTO.class));
    }

    /**
     * 새로운 채팅방 생성
     * @param roomName 방 이름
     * @param description 방 설명
     * @param participantName 사용자 이름
     * @return 생성된 채팅방 DTO
     */
    public ChatRoomDTO createRoom(String roomName, String description, String participantName) {
        ChatRoomDTO chatRoom = ChatRoomDTO.builder()
                .roomId(ChatRoomDTO.generateUniqueRoomId())
                .roomName(roomName)
                .description(description)
                .participantName(participantName)
                .lastMessage("채팅방이 생성되었습니다.")
                .lastMessageTime(java.time.LocalDateTime.now().toString())
                .isOccupied(false)
                .build();

        // Redis에 채팅방 저장
        redisTemplate.opsForHash().put(CHAT_ROOM_KEY, chatRoom.getRoomId(), chatRoom);
        redisTemplate.expire(CHAT_ROOM_KEY, ROOM_EXPIRATION_DAYS, TimeUnit.DAYS);

        return chatRoom;
    }

    /**
     * 채팅방 목록 조회
     * @return 모든 채팅방 목록
     */
    public List<ChatRoomDTO> getAvailableRooms() {
        return redisTemplate.opsForHash().values(CHAT_ROOM_KEY).stream()
                .map(room -> (ChatRoomDTO) room)
                .collect(Collectors.toList());    }

    /**
     * 특정 채팅방 조회
     * @param roomId 채팅방 ID
     * @return 채팅방 DTO
     */
    public ChatRoomDTO getRoom(String roomId) {
        return (ChatRoomDTO) redisTemplate.opsForHash().get(CHAT_ROOM_KEY, roomId);
    }

    /**
     * 마지막 메시지 업데이트
     * @param roomId 채팅방 ID
     * @param message 메시지 내용
     * @param timestamp 메시지 시간
     */
    public void updateLastMessage(String roomId, String message, String timestamp) {
        ChatRoomDTO chatRoom = (ChatRoomDTO) redisTemplate.opsForHash().get(CHAT_ROOM_KEY, roomId);
        if (chatRoom != null) {
            chatRoom.setLastMessage(message);
            chatRoom.setLastMessageTime(timestamp);
            redisTemplate.opsForHash().put(CHAT_ROOM_KEY, roomId, chatRoom);
        }
    }

    /**
     * 키워드를 사용한 채팅방 검색
     * @param keyword 검색 키워드
     * @return 검색 결과에 해당하는 채팅방 목록
     */
    public List<ChatRoomDTO> searchRooms(String keyword) {
        // 제목과 설명에서 검색
        List<ChatRoomDTO> filteredRooms = redisTemplate.opsForHash().values(CHAT_ROOM_KEY).stream()
                .map(room -> (ChatRoomDTO) room)
                .filter(room -> room.getRoomName().contains(keyword) || room.getDescription().contains(keyword))
                .collect(Collectors.toList());

        // 메시지에서 검색하여 해당 방 추가
        /*
        for (String roomId : chatRooms.keySet()) {
            List<Object> messages = redisTemplate.opsForHash().values("chat_message:" + roomId);
            boolean containsKeyword = messages.stream()
                    .anyMatch(message -> message.toString().contains(keyword));
            if (containsKeyword) {
                filteredRooms.add(chatRooms.get(roomId));
            }
        }
        */


        return filteredRooms;
    }

    /**
     * 의사 참여 여부 확인
     * @param roomId 채팅방 ID
     * @return 의사 참여 여부
     */
    public boolean isDoctorPresent(String roomId) {
        List<ChatParticipant> participants = chatParticipantRepository.findByRoomIdAndIsActive(roomId, true);
        return participants.stream().anyMatch(ChatParticipant::isDoctor);
    }

    /**
     * 채팅방 참여자 추가
     * @param roomId 채팅방 ID
     * @param participant 참여자 정보
     */
    public void addParticipant(String roomId, ChatParticipant participant) {
        chatParticipantRepository.save(participant);
    }

    /**
     * 사용자 퇴장 처리
     * @param roomId 채팅방 ID
     */
    public void handleUserExit(String roomId) {
        List<ChatParticipant> participants = chatParticipantRepository.findByRoomId(roomId);
        if (participants.stream().noneMatch(ChatParticipant::isActive)) {
            // 모든 참여자가 나간 경우 방 삭제
            redisTemplate.opsForHash().delete(CHAT_ROOM_KEY, roomId);
        }
    }

    /**
     * 의사 퇴장 처리
     * @param roomId 채팅방 ID
     */
    public void handleDoctorExit(String roomId) {
        ChatRoomDTO chatRoom = (ChatRoomDTO) redisTemplate.opsForHash().get(CHAT_ROOM_KEY, roomId);
        if (chatRoom != null) {
            chatRoom.setOccupied(false);
            redisTemplate.opsForHash().put(CHAT_ROOM_KEY, roomId, chatRoom);
        }
    }
}
