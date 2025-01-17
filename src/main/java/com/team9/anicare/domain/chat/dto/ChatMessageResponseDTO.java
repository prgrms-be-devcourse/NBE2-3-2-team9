package com.team9.anicare.domain.chat.dto;

import com.team9.anicare.domain.chat.entity.ChatMessage;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageResponseDTO {

    private Long messageId;
    private String roomId;
    private String senderName; // 발신자 이름 (관리자, 유저 닉네임, SYSTEM)
    private String content;
    private ChatMessage.MessageType type;
    private LocalDateTime sentAt;

    // 상대방 정보 추가
    private Long opponentId;
    private String opponentName;
    private String opponentProfileImg;
}
