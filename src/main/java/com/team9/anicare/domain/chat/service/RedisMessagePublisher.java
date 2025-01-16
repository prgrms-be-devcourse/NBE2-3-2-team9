package com.team9.anicare.domain.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team9.anicare.domain.chat.dto.ChatMessageRequestDTO;
import com.team9.anicare.domain.chat.dto.ChatMessageResponseDTO;
import com.team9.anicare.domain.chat.entity.ChatMessage;
import com.team9.anicare.domain.chat.entity.ChatRoom;
import com.team9.anicare.domain.chat.repository.ChatMessageRepository;
import com.team9.anicare.domain.chat.repository.ChatRoomRepository;
import com.team9.anicare.domain.user.model.User;
import com.team9.anicare.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * RedisMessagePublisher
 * - Redis Pub/Sub 채널로 메시지를 발행
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisMessagePublisher {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    private static final int MAX_RETRIES = 3;  // 최대 재시도 횟수
    private static final long INITIAL_RETRY_DELAY_MS = 1000;  // 초기 1초 대기
    private static final Long SYSTEM_USER_ID = 0L;
    private static final String SYSTEM_USER_NAME = "SYSTEM";


    /**
     * Redis 채널로 메시지를 발행하고 DB에 저장
     *
     * @param channel  Redis 채널명
     * @param senderId 발신자 ID
     * @param requestDTO 메시지 내용 DTO
     */
    public void publish(String channel, Long senderId, ChatMessageRequestDTO requestDTO) {
        int attempt = 0;
        long delay = INITIAL_RETRY_DELAY_MS;

        while (attempt < MAX_RETRIES) {
            try {
                // 메시지 저장
                ChatMessage chatMessage = saveChatMessage(senderId, requestDTO);

                // JSON 직렬화
                String messageJson = objectMapper.writeValueAsString(convertToResponseDTO(chatMessage));

                // Redis 채널로 발행
                redisTemplate.convertAndSend(channel, messageJson);
                log.info("Redis 채널에 메시지 발행 완료: channel={}, message={}", channel, messageJson);

                break;  // 성공 시 반복 중단

            } catch (Exception e) {
                attempt++;
                log.error("Redis 메시지 발행 실패 (시도 {}): {}", attempt, e.getMessage());

                if (attempt >= MAX_RETRIES) {
                    log.warn("Redis 발행 실패. DB에만 저장됨.");
                    return;
                }

                try {
                    TimeUnit.MILLISECONDS.sleep(delay);
                    delay *= 2;  // 대기 시간 두 배 증가 (백오프)
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("재시도 대기 중 인터럽트 발생", interruptedException);
                }
            }
        }
    }


    /**
     * 메시지를 DB에 저장
     */
    private ChatMessage saveChatMessage(Long senderId, ChatMessageRequestDTO requestDTO) {
        User sender = (senderId != null) ? findUserById(senderId) : getSystemUser();

        ChatRoom chatRoom = findChatRoomById(requestDTO.getRoomId());

        ChatMessage chatMessage = ChatMessage.builder()
                .sender(sender)
                .content(requestDTO.getContent())
                .type(requestDTO.getType())
                .chatRoom(chatRoom)
                .sentAt(LocalDateTime.now())
                .build();

        chatMessageRepository.save(chatMessage);

        // 마지막 메시지 업데이트
        updateLastMessage(chatRoom, chatMessage);

        return chatMessage;
    }


    /**
     * 채팅방의 마지막 메시지 업데이트
     */
    private void updateLastMessage(ChatRoom chatRoom, ChatMessage chatMessage) {
        chatRoom.setLastMessage(chatMessage.getContent());
        chatRoom.setLastMessageTime(chatMessage.getSentAt());
        chatRoomRepository.save(chatRoom);
    }


    /**
     * ChatMessage → ChatMessageResponseDTO 변환
     */
    private ChatMessageResponseDTO convertToResponseDTO(ChatMessage chatMessage) {
        return ChatMessageResponseDTO.builder()
                .messageId(chatMessage.getId())
                .roomId(chatMessage.getChatRoom().getRoomId())
                .senderName(chatMessage.getSender().getName())
                .content(chatMessage.getContent())
                .type(chatMessage.getType())
                .sentAt(chatMessage.getSentAt())
                .build();
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
     * 시스템 유저 생성
     */
    private User getSystemUser() {
        return User.builder().id(SYSTEM_USER_ID).name(SYSTEM_USER_NAME).build();
    }
}
