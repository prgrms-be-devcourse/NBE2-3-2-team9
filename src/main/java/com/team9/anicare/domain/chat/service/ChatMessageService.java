package com.team9.anicare.domain.chat.service;

import com.team9.anicare.domain.chat.dto.ChatMessageRequestDTO;
import com.team9.anicare.domain.chat.dto.ChatMessageResponseDTO;
import com.team9.anicare.domain.chat.entity.ChatMessage;
import com.team9.anicare.domain.chat.entity.ChatParticipant;
import com.team9.anicare.domain.chat.entity.ChatRoom;
import com.team9.anicare.domain.chat.repository.ChatMessageRepository;
import com.team9.anicare.domain.chat.repository.ChatParticipantRepository;
import com.team9.anicare.domain.chat.repository.ChatRoomRepository;
import com.team9.anicare.domain.user.model.User;
import com.team9.anicare.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ChatMessageService
 * - 채팅 메시지 송수신, 조회, 시스템 메시지 관리 등 메시지 관련 비즈니스 로직 처리
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ChatParticipantRepository chatParticipantRepository;


    /**
     * 일반 메시지 전송
     *
     * @param senderId  발신자 ID
     * @param requestDTO 메시지 요청 DTO
     * @return 전송된 메시지 응답 DTO
     */
    public ChatMessageResponseDTO sendMessage(Long senderId, ChatMessageRequestDTO requestDTO) {
        // 발신자 조회
        User sender = findUserById(senderId);

        // 채팅방 조회
        ChatRoom chatRoom = findChatRoomById(requestDTO.getRoomId());

        // 참여자 여부 확인
        validateParticipant(sender, chatRoom);

        // 수신자 조회 (선택적)
        User receiver = requestDTO.getReceiverId() != null
                ? findUserById(requestDTO.getReceiverId())
                : null;

        ChatMessage chatMessage = createChatMessage(sender, receiver, requestDTO, chatRoom);

        saveMessageAndUpdateRoom(chatMessage, chatRoom);

        // DTO 변환 및 반환
        return convertToResponseDTO(chatMessage);
    }

    /**
     * 시스템 메시지 전송
     * - 사용자의 입장/퇴장 등의 시스템 알림 메시지 전송
     *
     * @param roomId  채팅방 ID
     * @param content 시스템 메시지 내용
     * @param type    메시지 타입 (ENTER, EXIT)
     */
    public ChatMessageResponseDTO sendSystemMessage(String content, String roomId, ChatMessage.MessageType type) {
        ChatRoom chatRoom = findChatRoomById(roomId);

        ChatMessageRequestDTO requestDTO = ChatMessageRequestDTO.builder()
                .content(content)
                .type(type)
                .roomId(roomId)
                .build();

        ChatMessage systemMessage = createChatMessage(getSystemUser(), null, requestDTO, chatRoom);
        saveMessageAndUpdateRoom(systemMessage, chatRoom);

        return convertToResponseDTO(systemMessage);
    }


    /**
     * 특정 채팅방의 메시지 목록 조회
     * - 메시지를 시간순으로 조회
     *
     * @param roomId 조회할 채팅방 ID
     * @return 채팅 메시지 목록 DTO
     */
    public List<ChatMessageResponseDTO> getMessagesByRoom(String roomId) {
        ChatRoom chatRoom = findChatRoomById(roomId);

        return chatMessageRepository.findByChatRoomOrderBySentAtAsc(chatRoom).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }


    /**
     * 메시지 생성 (일반/시스템 공통)
     */
    private ChatMessage createChatMessage(User sender, User receiver, ChatMessageRequestDTO requestDTO, ChatRoom chatRoom) {
        return ChatMessage.builder()
                .sender(sender)
                .receiver(receiver)
                .content(requestDTO.getContent())
                .type(requestDTO.getType())
                .chatRoom(chatRoom)
                .sentAt(LocalDateTime.now())
                .build();
    }


    /**
     * 메시지 저장 및 채팅방의 마지막 메시지 업데이트
     */
    private void saveMessageAndUpdateRoom(ChatMessage chatMessage, ChatRoom chatRoom) {
        chatMessageRepository.save(chatMessage);
        chatRoom.setLastMessage(chatMessage.getContent());
        chatRoom.setLastMessageTime(chatMessage.getSentAt());
        chatRoomRepository.save(chatRoom);
    }


    /**
     * ChatMessage → ChatMessageResponseDTO 변환
     *
     * @param chatMessage 변환할 메시지 엔티티
     * @return 메시지 응답 DTO
     */
    private ChatMessageResponseDTO convertToResponseDTO(ChatMessage chatMessage) {
        return ChatMessageResponseDTO.builder()
                .messageId(chatMessage.getId())
                .roomId(chatMessage.getChatRoom().getRoomId())
                .senderName(chatMessage.getSender() != null ? chatMessage.getSender().getName() : "SYSTEM")
                .content(chatMessage.getContent())
                .type(chatMessage.getType())
                .sentAt(chatMessage.getSentAt())
                .build();
    }


    /**
     * 참여자 유효성 검사
     */
    private void validateParticipant(User sender, ChatRoom chatRoom) {
        boolean isParticipant = chatParticipantRepository.findByUserAndChatRoom(sender, chatRoom)
                .filter(ChatParticipant::isActive)
                .isPresent();

        if (!isParticipant) {
            throw new IllegalStateException("채팅방에 참여 중인 사용자가 아닙니다.");
        }
    }


    /**
     * 사용자 조회
     */
    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. ID: " + userId));
    }


    /**
     * 채팅방 조회
     */
    private ChatRoom findChatRoomById(String roomId) {
        return chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new EntityNotFoundException("채팅방을 찾을 수 없습니다. RoomID: " + roomId));
    }

    /**
     * 시스템 메시지를 위한 가상 SYSTEM 유저 생성
     */
    private User getSystemUser() {
        return User.builder().id(0L).name("SYSTEM").build();
    }
}
