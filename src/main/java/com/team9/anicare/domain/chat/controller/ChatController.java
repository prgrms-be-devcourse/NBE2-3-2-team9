package com.team9.anicare.domain.chat.controller;

import com.team9.anicare.domain.auth.security.CustomUserDetails;
import com.team9.anicare.domain.chat.dto.ChatMessageRequestDTO;
import com.team9.anicare.domain.chat.dto.ChatMessageResponseDTO;
import com.team9.anicare.domain.chat.service.ChatMessageService;
import com.team9.anicare.domain.chat.service.RedisMessagePublisher;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Tag(name = "chat", description = "채팅 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

    private final RedisMessagePublisher redisMessagePublisher;
    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * WebSocket을 통해 채팅 메시지 전송 및 Redis 발행
     */
    @MessageMapping("/chat/{roomId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public void sendMessage(@DestinationVariable String roomId,
                            ChatMessageRequestDTO requestDTO,
                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Long senderId = userDetails.getUserId();

            // 메시지 저장 및 발행
            ChatMessageResponseDTO savedMessage = chatMessageService.sendMessage(senderId, requestDTO);

            // Redis 채널 발행
            redisMessagePublisher.publish("chatroom:" + roomId, senderId, requestDTO);

            // WebSocket으로 메시지 브로드캐스트
            messagingTemplate.convertAndSend("/topic/chat/" + roomId, savedMessage);

        } catch (Exception e) {
            log.error("메시지 전송 중 오류 발생: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "메시지 전송 실패");
        }
    }


    /**
     * 채팅 로그 조회
     */
    @Operation(summary = "채팅방 메시지 로그 조회")
    @GetMapping("/rooms/{roomId}/messages")
    @PreAuthorize("hasRole('USER')")
    public List<ChatMessageResponseDTO> getMessagesByRoom(@PathVariable String roomId) {
        return chatMessageService.getMessagesByRoom(roomId);
    }
}
