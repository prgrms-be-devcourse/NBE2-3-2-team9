package com.team9.anicare.domain.chat.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.team9.anicare.domain.chat.dto.ChatRoomCreateRequestDTO;
import com.team9.anicare.domain.chat.dto.ChatRoomResponseDTO;
import com.team9.anicare.domain.chat.service.ChatRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "chatroom", description = "채팅방 API")
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;


    /**
     * 새로운 채팅방 생성
     */
    @PostMapping("/rooms")
    public ChatRoomResponseDTO createRoom(@AuthenticationPrincipal UserDetails userDetails,
                                          @RequestBody ChatRoomCreateRequestDTO requestDTO) {
        Long userId = Long.valueOf(userDetails.getUsername());  // 로그인한 사용자의 ID
        return chatRoomService.createChatRoom(userId, requestDTO);
    }


    /**
     * 채팅방 목록 조회
     * @return 대기 중인 채팅방 목록
     */
    @Operation(summary = "전체 채팅방 조회")
    @GetMapping("/rooms")
    public List<ChatRoomResponseDTO> getAllChatRooms() {
        return chatRoomService.getAllChatRooms();
    }


    /**
     * 특정 채팅방 조회
     * @param roomId 채팅방 ID
     * @return ChatRoomDTO
     */
    @Operation(summary = "특정 채팅방 조회")
    @GetMapping("/rooms/{roomId}")
    public ChatRoomResponseDTO getRoomById(@PathVariable String roomId) {
        return chatRoomService.getRoomById(roomId);
    }


    /**
     * 대기 중인(관리자가 없는) 채팅방 조회
     */
    @Operation(summary = "대기 중인 채팅방 조회", description = "관리자가 참여하지 않은 채팅방을 조회합니다.")
    @GetMapping("/rooms/waiting")
    public List<ChatRoomResponseDTO> getWaitingRooms() {
        return chatRoomService.getWaitingRooms();
    }


    /**
     * 키워드를 사용한 채팅방 검색
     * @param keyword 검색 키워드
     * @return 키워드가 포함된 채팅방 리스트
     */
    @Operation(summary = "채팅방 검색")
    @GetMapping("/rooms/search")
    public List<ChatRoomResponseDTO> searchChatRooms(@RequestParam String keyword) {
        return chatRoomService.searchChatRooms(keyword);
    }
}
