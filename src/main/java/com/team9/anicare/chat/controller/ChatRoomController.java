package com.team9.anicare.chat.controller;

import com.team9.anicare.chat.dto.ChatRoomDTO;
import com.team9.anicare.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    /**
     * 새로운 채팅방 생성
     * @param roomName 채팅방 이름
     * @param description 채팅방 설명
     * @param participantName 참여자 이름
     * @return 생성된 ChatRoomDTO
     */
    @PostMapping("/rooms")
    public ChatRoomDTO createRoom(
            @RequestParam String roomName,
            @RequestParam String description,
            @RequestParam String participantName) {
        ChatRoomDTO createdRoom = chatRoomService.createRoom(roomName, description, participantName);
        System.out.println("Created Room ID in Controller: " + createdRoom.getRoomId()); // 디버깅용
        return createdRoom;    }

    /**
     * 채팅방 목록 조회
     * @return 대기 중인 채팅방 목록
     */
    @GetMapping("/rooms")
    public List<ChatRoomDTO> getAvailableRooms() {
        return chatRoomService.getAvailableRooms();
    }

    /**
     * 특정 채팅방 조회
     * @param roomId 채팅방 ID
     * @return ChatRoomDTO
     */
    @GetMapping("/rooms/{roomId}")
    public ChatRoomDTO getRoom(@PathVariable String roomId) {
        return chatRoomService.getRoom(roomId);
    }
}
