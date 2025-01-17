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
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatServiceUtil chatServiceUtil;

    /**
     * 일반 메시지 전송
     *
     * @param senderId  발신자 ID
     * @param requestDTO 메시지 요청 DTO
     * @return 전송된 메시지 응답 DTO
     */
    public ChatMessageResponseDTO sendMessage(Long senderId, ChatMessageRequestDTO requestDTO) {
        validateMessageContent(requestDTO.getContent());
        return processAndSendMessage(senderId, requestDTO);
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
        ChatMessageRequestDTO requestDTO = ChatMessageRequestDTO.builder()
                .content(content)
                .type(type)
                .roomId(roomId)
                .build();

        return processAndSendMessage(null, requestDTO);  // senderId를 null로 넘기면 SYSTEM 메시지
    }


    /**
     * 공통 메시지 처리 로직
     */
    private ChatMessageResponseDTO processAndSendMessage(Long senderId, ChatMessageRequestDTO requestDTO) {
        User sender = (senderId != null) ? chatServiceUtil.findUserById(senderId) : chatServiceUtil.getSystemUser();
        ChatRoom chatRoom = chatServiceUtil.findChatRoomById(requestDTO.getRoomId());

        // 시스템 메시지일 경우 참여자 검증 생략
        if (senderId != null) {
            chatServiceUtil.validateParticipant(sender, chatRoom);
        }

        // ✅ 상대방(Receiver) 조회 (발신자가 아닌 사람)
        User receiver = chatParticipantRepository.findByChatRoom(chatRoom).stream()
                .filter(participant -> !participant.getUser().getId().equals(sender.getId()))
                .map(ChatParticipant::getUser)
                .findFirst()
                .orElse(null);

        ChatMessage chatMessage = createChatMessage(sender, receiver, requestDTO, chatRoom);
        saveMessageAndUpdateRoom(chatMessage, chatRoom);

        return convertToResponseDTO(chatMessage);
    }


    /**
     * 메시지 내용 유효성 검사
     * - 메시지가 비어있거나 1000자를 초과하면 예외 발생
     */
    private void validateMessageContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("메시지 내용은 비어 있을 수 없습니다.");
        }
        if (content.length() > 1000) {
            throw new IllegalArgumentException("메시지 내용은 1000자를 초과할 수 없습니다.");
        }
    }


    /**
     * 특정 채팅방의 메시지 목록 조회
     * - 메시지를 시간순으로 조회
     *
     * @param roomId 조회할 채팅방 ID
     * @return 채팅 메시지 목록 DTO
     */
    public List<ChatMessageResponseDTO> getMessagesByRoom(String roomId) {
        ChatRoom chatRoom = chatServiceUtil.findChatRoomById(roomId);

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

        ChatRoom chatRoom = chatMessage.getChatRoom();
        User sender = chatMessage.getSender();

        // 상대방 찾기 (발신자 제외)
        ChatParticipant opponentParticipant = chatParticipantRepository.findByChatRoom(chatRoom).stream()
                .filter(participant -> !participant.getUser().getId().equals(sender.getId()))
                .findFirst()
                .orElse(null);

        User opponent = (opponentParticipant != null) ? opponentParticipant.getUser() : null;

        return ChatMessageResponseDTO.builder()
                .messageId(chatMessage.getId())
                .roomId(chatMessage.getChatRoom().getRoomId())
                .senderName(chatMessage.getSender() != null ? chatMessage.getSender().getName() : "SYSTEM")
                .content(chatMessage.getContent())
                .type(chatMessage.getType())
                .sentAt(chatMessage.getSentAt())

                // 상대방 정보 추가
                .opponentId(opponent != null ? opponent.getId() : null)
                .opponentName(opponent != null ? opponent.getName() : "상대방 없음")
                .opponentProfileImg(opponent != null ? opponent.getProfileImg() : null)
                .build();
    }

}
