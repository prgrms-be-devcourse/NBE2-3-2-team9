package com.team9.anicare.domain.chat.controller;

import com.team9.anicare.common.dto.PageDTO;
import com.team9.anicare.common.dto.PageRequestDTO;
import com.team9.anicare.common.exception.ResultCode;
import com.team9.anicare.common.response.Result;
import com.team9.anicare.domain.auth.security.CustomUserDetails;
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

@Tag(name = "Admin Chat Room", description = "관리자 전용 채팅방 관리 API")
@RestController
@RequestMapping("/api/admin/chat")
@RequiredArgsConstructor
public class AdminChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ChatParticipantService chatParticipantService;


    @Operation(summary = "대기 중인 채팅방 조회")
    @GetMapping("/rooms/waiting")
    @PreAuthorize("hasRole('ADMIN')")
    public PageDTO<ChatRoomResponseDTO> getWaitingRooms(@ModelAttribute PageRequestDTO pageRequestDTO)
    {
        return chatRoomService.getWaitingRooms(pageRequestDTO);
    }


    @Operation(summary = "모든 채팅방 조회")
    @GetMapping("/rooms")
    @PreAuthorize("hasRole('ADMIN')")
    public PageDTO<ChatRoomResponseDTO> getAllChatRooms(
            @ModelAttribute PageRequestDTO pageRequestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails)
    {
        Long adminId = userDetails.getUserId();
        return chatRoomService.getAllChatRooms(adminId, pageRequestDTO);
    }


    @Operation(summary = "채팅방 검색 (Admin)")
    @GetMapping("/rooms/search")
    @PreAuthorize("hasRole('ADMIN')")
    public PageDTO<ChatRoomResponseDTO> searchChatRooms(
            @RequestParam String keyword,
            @ModelAttribute PageRequestDTO pageRequestDTO)
    {
        return chatRoomService.searchAllChatRooms(keyword, pageRequestDTO);
    }


    @Operation(summary = "채팅방 입장 (Admin)", description = "관리자가 채팅방에 입장합니다.")
    @PostMapping("/rooms/{roomId}/enter")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> enterChatRoomAsAdmin(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String roomId) {

        Long adminId = userDetails.getUserId();

        // ✅ 관리자 채팅방 입장 처리
        chatParticipantService.joinChatRoom(roomId, adminId, true);

        return ResponseEntity.ok("채팅방에 입장했습니다.");
    }

    @Operation(summary = "채팅방 상세 조회", description = "채팅방 ID를 이용해 채팅방 제목과 설명을 조회합니다.")
    @GetMapping("/rooms/{roomId}/detail")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ChatRoomDetailResponseDTO> getChatRoomDetail(@PathVariable String roomId) {
        ChatRoomDetailResponseDTO chatRoomDetail = chatRoomService.getChatRoomDetail(roomId);
        return ResponseEntity.ok(chatRoomDetail);
    }

    /**
     * 관리자 - 채팅방 퇴장
     */
    @Operation(summary = "채팅방 퇴장 (Admin)", description = "관리자가 채팅방에서 나갑니다.")
    @DeleteMapping("/rooms/{roomId}/exit")
    public ResponseEntity<Result<String>> exitChatRoomAsAdmin(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String roomId) {

        Long adminId = userDetails.getUserId();

        chatParticipantService.leaveChatRoom(roomId, adminId, true);

        Result<String> response = new Result<>(ResultCode.SUCCESS, "채팅방에서 성공적으로 퇴장했습니다.");

        return ResponseEntity.ok(response);    }
}
