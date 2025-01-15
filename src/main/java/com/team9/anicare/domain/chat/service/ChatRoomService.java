package com.team9.anicare.domain.chat.service;


import com.team9.anicare.domain.chat.dto.ChatRoomCreateRequestDTO;
import com.team9.anicare.domain.chat.dto.ChatRoomResponseDTO;
import com.team9.anicare.domain.chat.entity.ChatParticipant;
import com.team9.anicare.domain.chat.entity.ChatRoom;
import com.team9.anicare.domain.chat.repository.ChatMessageRepository;
import com.team9.anicare.domain.chat.repository.ChatParticipantRepository;
import com.team9.anicare.domain.chat.repository.ChatRoomRepository;
import com.team9.anicare.domain.user.model.User;
import com.team9.anicare.domain.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * - 채팅방 생성, 조회, 검색, 참여자 관리 등 채팅방 관련 비즈니스 로직을 처리하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatMessageRepository chatMessageRepository;


    /**
     * 채팅방 생성
     * - 사용자가 채팅방을 생성할 때 호출되는 메서드
     * - UUID를 이용해 고유한 roomId를 생성하고, DB에 저장
     *
     * @param userId     채팅방 생성자 ID
     * @param requestDTO 채팅방 이름 및 설명이 담긴 요청 DTO
     * @return 생성된 채팅방 정보 DTO
     */
    public ChatRoomResponseDTO createChatRoom(Long userId, ChatRoomCreateRequestDTO requestDTO) {
        // 채팅방 생성자 조회
        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 채팅방 생성 및 저장
        ChatRoom chatRoom = ChatRoom.builder()
                .roomId(ChatRoom.generateUniqueRoomId())  // 고유한 roomId 생성
                .roomName(requestDTO.getRoomName())       // 요청에서 받아온 채팅방 이름
                .description(requestDTO.getDescription()) // 요청에서 받아온 채팅방 설명
                .creator(creator)                         // 채팅방 생성자
                .occupied(false)                          // 기본값: 관리자가 참여하지 않은 상태
                .build();

        chatRoomRepository.save(chatRoom);

        // DTO로 변환하여 반환
        return convertToDTO(chatRoom);
    }


    /**
     * 전체 채팅방 조회
     * - DB에 저장된 모든 채팅방 정보를 조회
     *
     * @return 전체 채팅방 목록 DTO
     */
    public List<ChatRoomResponseDTO> getAllChatRooms() {
        return chatRoomRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    /**
     * 특정 채팅방 조회
     * - roomId를 기준으로 채팅방 조회
     *
     * @param roomId 조회할 채팅방 ID
     * @return 채팅방 정보 DTO
     */
    public ChatRoomResponseDTO getRoomById(String roomId) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        return convertToDTO(chatRoom);
    }


    /**
     * 대기 중인(관리자가 없는) 채팅방 조회
     *
     * @return 관리자가 없는 채팅방 목록
     */
    public List<ChatRoomResponseDTO> getWaitingRooms() {
        return chatRoomRepository.findByOccupiedFalse().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    /**
     * 관리자 참여 여부 확인
     * - 특정 채팅방에 관리자가 참여 중인지 확인
     *
     * @param roomId 채팅방 ID
     * @return 관리자가 참여 중이면 true, 아니면 false
     */
    public boolean isAdminPresent(String roomId) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        return chatParticipantRepository.findByChatRoomAndIsAdminTrue(chatRoom)
                .stream().anyMatch(ChatParticipant::isActive);
    }


    /**
     * 사용자 퇴장 처리
     * - 사용자가 채팅방에서 나갈 때 호출되는 메서드
     * - 관리자가 모두 나갔을 경우, 채팅방 상태를 비활성화(occupied = false)
     *
     * @param roomId 채팅방 ID
     * @param userId 퇴장한 사용자 ID
     */
    public void handleUserExit(String roomId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        ChatParticipant participant = chatParticipantRepository.findByUserAndChatRoom(user, chatRoom)
                .orElseThrow(() -> new IllegalArgumentException("참여자를 찾을 수 없습니다."));

        participant.setActive(false);
        chatParticipantRepository.save(participant);

        // 퇴장한 사용자가 관리자일 경우, 다른 관리자가 참여 중인지 확인 후 상태 변경
        if (participant.isAdmin() && !isAdminPresent(roomId)) {
            chatRoom.setOccupied(false);
            chatRoomRepository.save(chatRoom);
        }
    }


    /**
     * 키워드 기반 채팅방 검색
     * - 채팅방 이름, 설명, 메시지 내용에서 키워드를 검색
     *
     * @param keyword 검색할 키워드
     * @return 검색 결과에 해당하는 채팅방 목록 DTO
     */
    public List<ChatRoomResponseDTO> searchChatRooms (String keyword){
        // 1. 채팅방 이름 또는 설명에 키워드가 포함된 채팅방 검색
        List<ChatRoom> roomsByNameOrDescription = chatRoomRepository
                .findByRoomNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword);

        // 2. 메시지 내용에 키워드가 포함된 채팅방 ID 검색
        List<String> roomIdsByMessages = chatMessageRepository
                .findDistinctChatRoomIdsByKeyword(keyword);

        // 3. 메시지 내용에서 검색된 채팅방 조회
        List<ChatRoom> roomsByMessages = chatRoomRepository
                .findByRoomIdIn(roomIdsByMessages);

        // 4. 두 결과를 합치고 중복 제거
        List<ChatRoom> combinedRooms = roomsByNameOrDescription;
        combinedRooms.addAll(roomsByMessages);
        combinedRooms = combinedRooms.stream().distinct().collect(Collectors.toList());

        // 5. DTO로 변환 후 반환
        return combinedRooms.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    /**
     * ChatRoom -> ChatRoomResponseDTO 변환 메서드
     *
     * @param chatRoom 변환할 채팅방 엔티티
     * @return 채팅방 응답 DTO
     */
    private ChatRoomResponseDTO convertToDTO(ChatRoom chatRoom) {
        return ChatRoomResponseDTO.builder()
                .roomId(chatRoom.getRoomId())
                .roomName(chatRoom.getRoomName())
                .description(chatRoom.getDescription())
                .occupied(chatRoom.isOccupied())
                .lastMessage(chatRoom.getLastMessage())
                .lastMessageTime(chatRoom.getLastMessageTime())
                .createdAt(chatRoom.getCreatedAt())
                .build();
    }
}
