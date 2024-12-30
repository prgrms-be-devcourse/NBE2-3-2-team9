package com.team9.anicare.chat.service;


import com.team9.anicare.chat.dto.ChatRoomDTO;
import com.team9.anicare.chat.entity.ChatParticipant;
import com.team9.anicare.chat.repository.ChatParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ConcurrentHashMap<String, ChatRoomDTO> chatRooms = new ConcurrentHashMap<>();
    private final ChatParticipantRepository chatParticipantRepository;
    private final StringRedisTemplate redisTemplate;

    /**
     * 새로운 채팅방 생성
     * @param roomName 방 이름
     * @param description 방 설명
     * @param participantName 사용자 이름
     * @return 생성된 채팅방 DTO
     */    public ChatRoomDTO createRoom(String roomName, String description, String participantName) {
        ChatRoomDTO chatRoom = ChatRoomDTO.builder()
                .roomId(ChatRoomDTO.generateUniqueRoomId())
                .roomName(roomName)
                .description(description)
                .participantName(participantName)
                .lastMessage("채팅방이 생성되었습니다.")
                .lastMessageTime(java.time.LocalDateTime.now().toString())
                .isOccupied(false)
                .build();

        chatRooms.put(chatRoom.getRoomId(), chatRoom);
        return chatRoom;
    }

    /**
     * 채팅방 목록 조회
     * @return 모든 채팅방 목록
     */
    public List<ChatRoomDTO> getAvailableRooms() {
        return new ArrayList<>(chatRooms.values());
    }

    /**
     * 특정 채팅방 조회
     * @param roomId 채팅방 ID
     * @return 채팅방 DTO
     */
    public ChatRoomDTO getRoom(String roomId) {
        return chatRooms.get(roomId);
    }

    /**
     * 마지막 메시지 업데이트
     * @param roomId 채팅방 ID
     * @param message 메시지 내용
     * @param timestamp 메시지 시간
     */
    public void updateLastMessage(String roomId, String message, String timestamp) {
        ChatRoomDTO chatRoom = chatRooms.get(roomId);
        if (chatRoom != null) {
            chatRoom.setLastMessage(message);
            chatRoom.setLastMessageTime(timestamp);
        }
    }

    /**
     * 키워드를 사용한 채팅방 검색
     * @param keyword 검색 키워드
     * @return 검색 결과에 해당하는 채팅방 목록
     */
    public List<ChatRoomDTO> searchRooms(String keyword) {
        // 제목과 설명에서 검색
        List<ChatRoomDTO> filteredRooms = chatRooms.values().stream()
                .filter(room -> room.getRoomName().contains(keyword) || room.getDescription().contains(keyword))
                .collect(Collectors.toList());

        // 메시지에서 검색하여 해당 방 추가
        for (String roomId : chatRooms.keySet()) {
            List<Object> messages = redisTemplate.opsForHash().values("chat_message:" + roomId);
            boolean containsKeyword = messages.stream()
                    .anyMatch(message -> message.toString().contains(keyword));
            if (containsKeyword) {
                filteredRooms.add(chatRooms.get(roomId));
            }
        }

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
            chatRooms.remove(roomId);
        }
    }

    /**
     * 의사 퇴장 처리
     * @param roomId 채팅방 ID
     */
    public void handleDoctorExit(String roomId) {
        ChatRoomDTO room = chatRooms.get(roomId);
        if (room != null) {
            room.setOccupied(false); // 대기 상태로 전환
        }
    }
}
