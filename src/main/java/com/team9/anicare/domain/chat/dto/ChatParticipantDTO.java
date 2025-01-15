package com.team9.anicare.domain.chat.dto;

import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatParticipantDTO {

    private Long participantId;       // 참여자 고유 ID
    private String userName;          // 참여자 이름
    private boolean isAdmin;          // 관리자 여부
    private boolean isActive;         // 현재 채팅방 참여 여부
    private LocalDateTime joinedAt;   // 채팅방 참여 시간
}
