package com.team9.anicare.domain.chat.controller;

import com.team9.anicare.domain.chat.service.ChatLogService;
import com.team9.anicare.domain.chat.service.ChatRoomService;
import com.team9.anicare.domain.chat.service.RedisMessagePublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "chat", description = "채팅 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

    private final RedisMessagePublisher redisMessagePublisher;
    private final ChatRoomService chatRoomService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;
    private final ChatLogService chatLogService;

    /**
     * WebSocket 메시지 처리
     * @param message 클라이언트로부터 전달된 메시지
     */
    @MessageMapping("/message")
    public void handleMessage(ChatMessageDTO message) throws Exception {
        // Redis Pub/Sub 채널 이름
        String channel = "chatroom:" + message.getRoomId();

        // 메시지 Redis로 발행
        redisMessagePublisher.publish(channel, objectMapper.writeValueAsString(message));

        // 특정 receiver가 있는 경우 WebSocket으로 전송
        if (message.getReceiver() != null) {
            messagingTemplate.convertAndSendToUser(
                    message.getReceiver(),
                    "/topic/chat/" + message.getRoomId(),
                    message
            );
        } else {
            // 브로드캐스트 (방에 연결된 모든 클라이언트에게 전송)
            messagingTemplate.convertAndSend(
                    "/topic/chat/" + message.getRoomId(),
                    message
            );
        }

        // Redis에 메시지 저장
        chatLogService.saveChatMessage(
                message.getRoomId(),
                message
        );

        // 마지막 메시지 및 시간 업데이트
        chatRoomService.updateLastMessage(
                message.getRoomId(),
                message.getContent(),
                java.time.LocalDateTime.now().toString()
        );
    }

    // 채팅 로그 조회
    @Operation(summary = "채팅방 로그 조회")
    @GetMapping("/rooms/{roomId}/logs")
    public List<ChatMessageDTO> getChatLogs(@PathVariable String roomId) {
        return chatLogService.getChatLogs(roomId);
    }

    // 사용자 퇴장 처리
    @Operation(summary = "채팅방 나가기")
    @PostMapping("/rooms/{roomId}/exit")
    public void exitChatRoom(@PathVariable String roomId, @RequestParam boolean isDoctor) {
        if (isDoctor) {
            chatRoomService.handleDoctorExit(roomId);
        } else {
            chatRoomService.handleUserExit(roomId);
        }
    }

    /**
     * 클라이언트로부터 메시지를 받아 브로드캐스트
     * @param roomId 채팅방 ID
     * @param message 클라이언트가 보낸 메시지
     */
    @MessageMapping("/chat/{roomId}")
    public void sendMessage(@DestinationVariable String roomId, ChatMessageDTO message) {

        chatLogService.saveChatMessage(roomId, message);

        // 메시지를 구독한 클라이언트들에게 브로드캐스트
        messagingTemplate.convertAndSend("/topic/chat/" + roomId, message);
    }
}
