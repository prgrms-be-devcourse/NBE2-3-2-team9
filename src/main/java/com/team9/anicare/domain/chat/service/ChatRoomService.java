package com.team9.anicare.domain.chat.service;


import com.team9.anicare.domain.chat.dto.ChatRoomCreateRequestDTO;
import com.team9.anicare.domain.chat.dto.ChatRoomResponseDTO;
import com.team9.anicare.domain.chat.entity.ChatMessage;
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
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatMessageService chatMessageService;
    private final ChatServiceUtil chatServiceUtil;


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
        User creator = chatServiceUtil.findUserById(userId);


        // 채팅방 생성 및 저장
        ChatRoom chatRoom = ChatRoom.builder()
                .roomId(ChatRoom.generateUniqueRoomId())  // 고유한 roomId 생성
                .roomName(requestDTO.getRoomName())       // 요청에서 받아온 채팅방 이름
                .description(requestDTO.getDescription()) // 요청에서 받아온 채팅방 설명
                .creator(creator)                         // 채팅방 생성자
                .occupied(false)                          // 기본값: 관리자가 참여하지 않은 상태
                .build();

        chatRoomRepository.save(chatRoom);

        chatMessageService.sendSystemMessage(
                String.format("채팅방이 생성되었습니다. 방 제목: %s, 설명: %s", requestDTO.getRoomName(), requestDTO.getDescription()),
                chatRoom.getRoomId(),
                ChatMessage.MessageType.SYSTEM
        );

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
     * ✅ 관리자 전용 - 전체 채팅방 검색
     * - 채팅방 이름, 설명, 메시지 내용에서 키워드를 검색
     */
    public List<ChatRoomResponseDTO> searchAllChatRooms(String keyword) {
        // 1. 채팅방 이름 또는 설명 검색
        List<ChatRoom> roomsByNameOrDescription = chatRoomRepository
                .findByRoomNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword);

        // 2. 메시지 내용에 키워드가 포함된 채팅방 ID 검색
        List<String> roomIdsByMessages = chatMessageRepository
                .findDistinctChatRoomIdsByKeyword(keyword);

        // 3. 메시지 내용에서 검색된 채팅방 조회
        List<ChatRoom> roomsByMessages = chatRoomRepository
                .findByRoomIdIn(roomIdsByMessages);

        // 4. 두 결과 합치고 중복 제거
        roomsByNameOrDescription.addAll(roomsByMessages);
        List<ChatRoom> combinedRooms = roomsByNameOrDescription.stream().distinct().collect(Collectors.toList());

        // 5. DTO 변환
        return combinedRooms.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    /**
     * ✅ 사용자 전용 - 본인이 참여하거나 생성한 채팅방 검색
     * - 채팅방 이름, 설명, 메시지 내용에서 키워드를 검색
     */
    public List<ChatRoomResponseDTO> searchUserChatRooms(Long userId, String keyword) {
        // 1. 사용자가 참여 중인 채팅방 ID 조회
        List<String> participantRoomIds = chatParticipantRepository.findRoomIdsByUserId(userId);

        // 2. 사용자가 생성한 채팅방 검색 (이름 or 설명에 키워드 포함)
        List<ChatRoom> createdRooms = chatRoomRepository
                .findByCreatorIdAndRoomNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(userId, keyword, keyword);

        List<ChatRoom> roomsParticipatedByUser = chatRoomRepository
                .findByRoomIdIn(participantRoomIds)
                .stream()
                .filter(room -> room.getRoomName().toLowerCase().contains(keyword.toLowerCase()) ||
                        room.getDescription().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());

        // 3. 참여 중인 채팅방 중에서 키워드가 포함된 채팅방 검색
        List<ChatRoom> participantRooms = chatRoomRepository
                .findByRoomIdIn(participantRoomIds)
                .stream()
                .filter(room -> room.getRoomName().toLowerCase().contains(keyword.toLowerCase()) ||
                        room.getDescription().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());

        // 4. 결과 합치기 및 중복 제거
        createdRooms.addAll(participantRooms);
        List<ChatRoom> combinedRooms = createdRooms.stream().distinct().collect(Collectors.toList());

        // 5. DTO 변환 및 반환
        return combinedRooms.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    /**
     * 사용자 전용 - 본인이 생성한 채팅방 조회
     *
     * @param userId 사용자 ID
     * @return 사용자가 생성한 채팅방 정보 DTO (없으면 예외 발생)
     */
    public ChatRoomResponseDTO getRoomByUserId(Long userId) {
        // 사용자가 생성한 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findByCreatorId(userId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("생성한 채팅방이 없습니다."));

        // DTO로 변환 후 반환
        return convertToDTO(chatRoom);
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
