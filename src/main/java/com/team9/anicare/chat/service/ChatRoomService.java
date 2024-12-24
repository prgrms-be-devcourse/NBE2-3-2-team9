package com.team9.anicare.chat.service;


import com.team9.anicare.chat.dto.ChatRoomDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatRoomService {

    private final ConcurrentHashMap<String, ChatRoomDTO> chatRooms = new ConcurrentHashMap<>();

    // 새로운 채팅방 생성
    public ChatRoomDTO createRoom(String roomName, String description, String participantName) {
        ChatRoomDTO chatRoom = ChatRoomDTO.create(roomName, description, participantName);
        chatRooms.put(chatRoom.getRoomId(), chatRoom);
        return chatRoom;
    }

    // 채팅방 목록 조회
    public List<ChatRoomDTO> getAvailableRooms() {
        return new ArrayList<>(chatRooms.values());
    }

    // 특정 채팅방 조회
    public ChatRoomDTO getRoom(String roomId) {
        return chatRooms.get(roomId);
    }

    // 마지막 메시지 업데이트
    public void updateLastMessage(String roomId, String message, String timestamp) {
        ChatRoomDTO chatRoom = chatRooms.get(roomId);
        if (chatRoom != null) {
            chatRoom.setLastMessage(message);
            chatRoom.setLastMessageTime(timestamp);
        }
    }
}
