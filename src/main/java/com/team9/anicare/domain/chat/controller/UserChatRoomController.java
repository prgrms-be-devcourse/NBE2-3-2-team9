package com.team9.anicare.domain.chat.controller;

import com.team9.anicare.common.dto.PageDTO;
import com.team9.anicare.common.dto.PageRequestDTO;
import com.team9.anicare.common.exception.ResultCode;
import com.team9.anicare.common.response.Result;
import com.team9.anicare.domain.auth.security.CustomUserDetails;
import com.team9.anicare.domain.chat.dto.ChatRoomCreateRequestDTO;
import com.team9.anicare.domain.chat.dto.ChatRoomDetailResponseDTO;
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
    public PageDTO<ChatRoomResponseDTO> getMyChatRooms(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @ModelAttribute PageRequestDTO pageRequestDTO) {
        Long userId = userDetails.getUserId();
        return chatRoomService.getRoomsByUserId(userId, pageRequestDTO);
    }


    @Operation(summary = "내 채팅방 검색", description = "사용자가 본인이 참여한 채팅방을 검색")
    @GetMapping("/rooms/search")
    @PreAuthorize("hasRole('USER')")
    public PageDTO<ChatRoomResponseDTO> searchMyChatRooms(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam String keyword,
            @ModelAttribute PageRequestDTO pageRequestDTO) {
        Long userId = userDetails.getUserId();
        return chatRoomService.searchUserChatRooms(userId, keyword, pageRequestDTO);
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

    @Operation(summary = "채팅방 상세 조회", description = "채팅방 ID를 이용해 채팅방 제목과 설명을 조회합니다.")
    @GetMapping("/rooms/{roomId}/detail")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ChatRoomDetailResponseDTO> getChatRoomDetail(@PathVariable String roomId) {
        ChatRoomDetailResponseDTO chatRoomDetail = chatRoomService.getChatRoomDetail(roomId);
        return ResponseEntity.ok(chatRoomDetail);
    }



    @Operation(summary = "채팅방 퇴장 (User)", description = "사용자가 채팅방에서 나갑니다.")
    @DeleteMapping("/rooms/{roomId}/exit")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Result<String>> exitChatRoom(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String roomId) {

        Long userId = userDetails.getUserId();

        chatParticipantService.leaveChatRoom(roomId, userId, false);

        Result<String> response = new Result<>(ResultCode.SUCCESS, "채팅방에서 성공적으로 퇴장했습니다.");

        return ResponseEntity.ok(response);    }
}