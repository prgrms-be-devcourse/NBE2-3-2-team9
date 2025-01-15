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
    private static final long RETRY_DELAY_MS = 1000;  // 재시도 간격 (밀리초)

    /**
     * Redis 채널로 메시지를 발행하고 DB에 저장
     *
     * @param channel  Redis 채널명
     * @param senderId 발신자 ID
     * @param requestDTO 메시지 내용 DTO
     */
    public void publish(String channel, Long senderId, ChatMessageRequestDTO requestDTO) {
        int attempt = 0;

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
                    throw new RuntimeException("Redis 메시지 발행 실패: " + e.getMessage(), e);
                }

                try {
                    TimeUnit.MILLISECONDS.sleep(RETRY_DELAY_MS);  // 재시도 전 대기
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
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("발신자를 찾을 수 없습니다."));

        ChatRoom chatRoom = chatRoomRepository.findByRoomId(requestDTO.getRoomId())
                .orElseThrow(() -> new EntityNotFoundException("채팅방을 찾을 수 없습니다."));

        ChatMessage chatMessage = ChatMessage.builder()
                .sender(sender)
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

        return chatMessage;
    }


    /**
     * ChatMessage → ChatMessageResponseDTO 변환
     */
    private ChatMessageResponseDTO convertToResponseDTO(ChatMessage chatMessage) {
        return ChatMessageResponseDTO.builder()
                .messageId(chatMessage.getId())
                .senderName(chatMessage.getSender().getName())
                .content(chatMessage.getContent())
                .type(chatMessage.getType())
                .sentAt(chatMessage.getSentAt())
                .build();
    }
}
