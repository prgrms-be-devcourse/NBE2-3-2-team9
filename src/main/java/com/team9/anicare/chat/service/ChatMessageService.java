package com.team9.anicare.chat.service;

import com.team9.anicare.chat.dto.ChatMessageDTO;
import org.springframework.stereotype.Service;

@Service
public class ChatMessageService {

    // 메시지 생성 로직
    public ChatMessageDTO createMessage(String sender, String receiver, String roomId, String content, ChatMessageDTO.MessageType type) {
        ChatMessageDTO message = new ChatMessageDTO();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setRoomId(roomId);
        message.setContent(content);
        message.setType(type);
        return message;
    }
}
