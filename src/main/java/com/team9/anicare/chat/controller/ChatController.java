package com.team9.anicare.chat.controller;

import com.team9.anicare.chat.dto.ChatMessageDTO;
import com.team9.anicare.chat.dto.ChatRoomDTO;
import com.team9.anicare.chat.service.ChatLogService;
import com.team9.anicare.chat.service.ChatRoomService;
import com.team9.anicare.chat.service.RedisMessagePublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
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
    @MessageMapping("/chat/message")
    public void handleMessage(ChatMessageDTO message) throws Exception {
        // Redis Pub/Sub 채널 이름
        String channel = "chatRoom:" + message.getRoomId();

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

        // 마지막 메시지 및 시간 업데이트
        chatRoomService.updateLastMessage(
                message.getRoomId(),
                message.getContent(),
                java.time.LocalDateTime.now().toString()
        );
    }

    // 키워드를 사용한 채팅방 검색
    @GetMapping("/chat/search")
    public List<ChatRoomDTO> searchChatRooms(@RequestParam String keyword) {
        return chatRoomService.searchRooms(keyword);
    }

    // 채팅 로그 조회
    @GetMapping("/rooms/{roomId}/logs")
    public List<String> getChatLogs(@PathVariable String roomId) {
        return chatLogService.getChatLogs(roomId);
    }

    // 사용자 퇴장 처리
    @PostMapping("/rooms/{roomId}/exit")
    public void exitChatRoom(@PathVariable String roomId, @RequestParam boolean isDoctor) {
        if (isDoctor) {
            chatRoomService.handleDoctorExit(roomId);
        } else {
            chatRoomService.handleUserExit(roomId);
        }
    }
}
