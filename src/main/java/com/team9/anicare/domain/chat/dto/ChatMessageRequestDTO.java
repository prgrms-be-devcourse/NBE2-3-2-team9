package com.team9.anicare.domain.chat.dto;

import com.team9.anicare.domain.chat.entity.ChatMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageRequestDTO {

    @NotBlank(message = "메시지 내용은 필수입니다.")
    private String content;

    @NotNull(message = "메시지 유형은 필수입니다.")
    private ChatMessage.MessageType type; // ENTER, TALK, EXIT, SYSTEM

    @NotBlank(message = "채팅방 ID는 필수입니다.")
    private String roomId;

    private Long receiverId;  // 수신자 ID (선택)

    /**
     * 메시지 타입에 따라 기본 메시지를 설정하는 메서드
     */
    public static ChatMessageRequestDTO createWithType(String senderName, String roomId, ChatMessage.MessageType type) {
        String content = switch (type) {
            case ENTER -> senderName + "님과의 상담이 시작되었습니다.";
            case EXIT -> senderName + "님과의 상담이 종료되었습니다.";
            default -> "";
        };
        return ChatMessageRequestDTO.builder()
                .content(content)
                .type(type)
                .roomId(roomId)
                .build();
    }
}
