package com.team9.anicare.domain.chat.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomResponseDTO {

    private String roomId;           // 채팅방 ID
    private String roomName;         // 채팅방 이름
    private String description;      // 채팅방 설명
    private boolean occupied;        // 관리자 참여 여부
    private String lastMessage;      // 마지막 메시지
    private LocalDateTime lastMessageTime;
    private LocalDateTime createdAt;
}
