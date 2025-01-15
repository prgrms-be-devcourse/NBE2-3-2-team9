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
    private final ChatParticipant chatParticipant;


    /**
     * 일반 메시지 전송
     *
     * @param senderId  발신자 ID
     * @param requestDTO 메시지 요청 DTO
     * @return 전송된 메시지 응답 DTO
     */
    public ChatMessageResponseDTO sendMessage(Long senderId, ChatMessageRequestDTO requestDTO) {
        // 발신자 조회
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("발신자를 찾을 수 없습니다."));

        // 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(requestDTO.getRoomId())
                .orElseThrow(() -> new EntityNotFoundException("채팅방을 찾을 수 없습니다."));

        // 참여자 여부 확인
        boolean isParticipant = chatParticipantRepository.findByUserAndChatRoom(sender, chatRoom)
                .filter(ChatParticipant::isActive)
                .isPresent();

        if (!isParticipant) {
            throw new IllegalStateException("채팅방에 참여 중인 사용자가 아닙니다.");
        }

        // 수신자 조회 (선택적)
        User receiver = null;
        if (requestDTO.getReceiverId() != null) {
            receiver = userRepository.findById(requestDTO.getReceiverId())
                    .orElseThrow(() -> new EntityNotFoundException("수신자를 찾을 수 없습니다."));
        }

        ChatMessage chatMessage = ChatMessage.builder()
                .sender(sender)
                .receiver(receiver)
                .content(requestDTO.getContent())
                .type(requestDTO.getType())
                .chatRoom(chatRoom)
                .sentAt(LocalDateTime.now())
                .build();

        chatMessageRepository.save(chatMessage);

        // 마지막 메시지 업데이트
        chatRoom.setLastMessage(chatMessage.getContent());
        chatRoom.setLastMessageTime(chatMessage.getSentAt());
        chatRoomRepository.save(chatRoom);

        // DTO 변환 및 반환
        return convertToResponseDTO(chatMessage);
    }

    /**
     * 🔹 시스템 메시지 전송
     * - 사용자의 입장/퇴장 등의 시스템 알림 메시지 전송
     *
     * @param roomId  채팅방 ID
     * @param content 시스템 메시지 내용
     * @param type    메시지 타입 (ENTER, EXIT)
     */
    public ChatMessageResponseDTO sendSystemMessage(String content, String roomId, ChatMessage.MessageType type) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new EntityNotFoundException("채팅방을 찾을 수 없습니다."));

        // 시스템 메시지 생성
        User systemUser = User.builder().id(0L).name("SYSTEM").build();

        ChatMessage systemMessage = ChatMessage.builder()
                .sender(systemUser)
                .content(content)
                .type(type)
                .chatRoom(chatRoom)
                .sentAt(LocalDateTime.now())
                .build();

        chatMessageRepository.save(systemMessage);

        return ChatMessageResponseDTO.systemMessage(content, type);
    }


    /**
     * 특정 채팅방의 메시지 목록 조회
     * - 메시지를 시간순으로 조회
     *
     * @param roomId 조회할 채팅방 ID
     * @return 채팅 메시지 목록 DTO
     */
    public List<ChatMessageResponseDTO> getMessagesByRoom(String roomId) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new EntityNotFoundException("채팅방을 찾을 수 없습니다."));

        return chatMessageRepository.findByChatRoomOrderBySentAtAsc(chatRoom).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
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
                .senderName(chatMessage.getSender() != null ? chatMessage.getSender().getName() : "SYSTEM")
                .content(chatMessage.getContent())
                .type(chatMessage.getType())
                .sentAt(chatMessage.getSentAt())
                .build();
    }
}
