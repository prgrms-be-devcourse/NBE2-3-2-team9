package com.team9.anicare.domain.chat.controller;

import com.team9.anicare.domain.auth.security.CustomUserDetails;
import com.team9.anicare.domain.chat.dto.ChatRoomCreateRequestDTO;
import com.team9.anicare.domain.chat.dto.ChatRoomResponseDTO;
import com.team9.anicare.domain.chat.service.ChatParticipantService;
import com.team9.anicare.domain.chat.service.ChatRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "User Chat Room", description = "사용자 전용 채팅방 관리 API")
@RestController
@RequestMapping("/api/user/chat")
@RequiredArgsConstructor
public class UserChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ChatParticipantService chatParticipantService;

    @Operation(summary = "채팅방 생성 (User)")
    @PostMapping("/rooms")
    @PreAuthorize("hasRole('USER')")
    public ChatRoomResponseDTO createRoom(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody ChatRoomCreateRequestDTO requestDTO)
    {
        Long userId = userDetails.getUserId();
        return chatRoomService.createChatRoom(userId, requestDTO);
    }

    @Operation(summary = "내 채팅방 조회")
    @GetMapping("/rooms")
    @PreAuthorize("hasRole('USER')")
    public List<ChatRoomResponseDTO> getMyChatRoom(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size)
    {
        Long userId = userDetails.getUserId();
        return chatRoomService.getRoomsByUserId(userId);
    }


    @Operation(summary = "내 채팅방 검색", description = "사용자가 본인이 참여한 채팅방을 검색하는 API입니다.")
    @GetMapping("/rooms/search")
    @PreAuthorize("hasRole('USER')")
    public List<ChatRoomResponseDTO> searchMyChatRooms(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size)
    {
        Long userId = userDetails.getUserId();
        return chatRoomService.searchUserChatRooms(userId, keyword);
    }


    @Operation(summary = "채팅방 입장 (User)", description = "사용자가 채팅방에 입장합니다.")
    @PostMapping("/rooms/{roomId}/enter")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> enterChatRoom(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String roomId) {

        Long userId = userDetails.getUserId();

        // ✅ 사용자 채팅방 입장 처리
        chatParticipantService.joinChatRoom(roomId, userId, false);

        return ResponseEntity.ok("채팅방에 입장했습니다.");
    }


    @Operation(summary = "채팅방 퇴장 (User)", description = "사용자가 채팅방에서 나갑니다.")
    @PostMapping("/rooms/{roomId}/exit")
    public ResponseEntity<String> exitChatRoom(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String roomId) {

        Long userId = userDetails.getUserId();

        chatParticipantService.leaveChatRoom(roomId, userId, false);

        return ResponseEntity.ok("채팅방에서 성공적으로 퇴장했습니다.");
    }
}