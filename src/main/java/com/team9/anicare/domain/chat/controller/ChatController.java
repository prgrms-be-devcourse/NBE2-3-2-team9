package com.team9.anicare.domain.chat.controller;

import com.team9.anicare.domain.chat.dto.ChatMessageRequestDTO;
import com.team9.anicare.domain.chat.dto.ChatMessageResponseDTO;
import com.team9.anicare.domain.chat.entity.ChatMessage;
import com.team9.anicare.domain.chat.service.ChatMessageService;
import com.team9.anicare.domain.chat.service.ChatRoomService;
import com.team9.anicare.domain.chat.service.RedisMessagePublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

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
    public void sendMessage(@DestinationVariable String roomId, ChatMessageRequestDTO requestDTO) {
        try {
            // 메시지 저장 및 발행
            ChatMessageResponseDTO savedMessage = chatMessageService.sendMessage(requestDTO.getReceiverId(), requestDTO);

            // Redis 채널 발행
            redisMessagePublisher.publish("chatroom:" + roomId, requestDTO.getReceiverId(), requestDTO);

            // WebSocket으로 메시지 브로드캐스트
            messagingTemplate.convertAndSend("/topic/chat/" + roomId, savedMessage);

        } catch (Exception e) {
            log.error("메시지 전송 중 오류 발생: {}", e.getMessage(), e);
        }
    }


    // 채팅 로그 조회
    @Operation(summary = "채팅방 메시지 로그 조회")
    @GetMapping("/rooms/{roomId}/messages")
    public List<ChatMessageResponseDTO> getMessagesByRoom(@PathVariable String roomId) {
        return chatMessageService.getMessagesByRoom(roomId);
    }


    // 사용자 퇴장 처리
    @Operation(summary = "채팅방 퇴장")
    @PostMapping("/rooms/{roomId}/exit")
    public void exitChatRoom(@PathVariable String roomId, @RequestParam Long userId, @RequestParam boolean isAdmin) {
        String content = isAdmin ? "관리자가 퇴장했습니다." : "사용자가 퇴장했습니다.";
        chatMessageService.sendSystemMessage(content, roomId, ChatMessage.MessageType.EXIT);
    }
}
